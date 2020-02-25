package ca.gc.aafc.objectstore.api.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.MetadataManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.DcType;
import ca.gc.aafc.objectstore.api.entities.MetadataManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.entities.ObjectSubtype;
import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ResourceNotFoundException;

@Mapper(componentModel = "spring")
public abstract class ObjectStoreMetadataMapper {

  public static final ObjectStoreMetadataMapper INSTANCE = Mappers.getMapper(ObjectStoreMetadataMapper.class);

  @Autowired
  private BaseDAO dao;

  @Mapping(target = "managedAttribute", qualifiedByName="metadataManagedAttributeIdOnly")
  @Mapping(target = "acSubType", qualifiedByName="objectSubTypeEntityToDto")
  public abstract ObjectStoreMetadataDto toDto(ObjectStoreMetadata entity, @Context Function<String, Boolean> isAvailable, @Context CycleAvoidingMappingContext context);
  

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "managedAttribute", ignore = true)
  @Mapping(target = "acMetadataCreator", ignore = true)
  @Mapping(target = "acDerivedFrom", ignore = true)
  @Mapping(target = "dcCreator", ignore = true)
  @Mapping(target = "acSubType", qualifiedByName="objectSubTypeToEntity")
  public abstract ObjectStoreMetadata toEntity(ObjectStoreMetadataDto dto);
  
  @InheritConfiguration
  public abstract void updateObjectStoreMetadataFromDto(ObjectStoreMetadataDto dto, @MappingTarget ObjectStoreMetadata entity);
   
  /**
   * Used to avoid cyclic reference since managedAttribute points back to ObjectStoreMetadata.
   * @param mma list to transform
   * @param isAvailable function to check if a specific attribute is available (can be loaded without lazy-loading)
   * @param context mapping context used to avoid cyclic references
   * @return
   */
  @Named("metadataManagedAttributeIdOnly")
  protected List<MetadataManagedAttributeDto> metadataManagedAttributeListToMetadataManagedAttributeDtoList(List<MetadataManagedAttribute> mma, @Context Function<String, Boolean> isAvailable, 
      @Context CycleAvoidingMappingContext context) {
    if (mma == null || isAvailable == null || !isAvailable.apply("managedAttribute")) {
      return null;
    }
    return MetadataManagedAttributeMapper.INSTANCE.toDtoList(mma, context);
  }

  @Named("objectSubTypeEntityToDto")
  protected String objectSubTypeEntityToDto(
      ObjectSubtype entity,
      @Context Function<String, Boolean> isAvailable,
      @Context CycleAvoidingMappingContext context) {

    if (entity == null || isAvailable == null || !isAvailable.apply("acSubType")) {
      return null;
    }
    return entity.getAcSubtype() + "/" + entity.getDcType().getValue();
  }

  @Named("objectSubTypeToEntity")
  protected ObjectSubtype objectSubTypeToEntity(String oSubType) {

    if (oSubType == null) {
      return null;
    }

    String[] parsedValue = oSubType.split("/");
    if (parsedValue.length != 2) {
      throw new BadRequestException("acSubType malformed, expecting {acSubType/dcType} example thumbnail/Image");
    }

    HashMap<String, Object> propertyMap = new HashMap<>();
    propertyMap.put("acSubtype", parsedValue[0]);
    propertyMap.put("dcType", DcType.fromValue(parsedValue[1]).orElse(null));

    List<ObjectSubtype> subtypes = dao.findAllWhere(ObjectSubtype.class, propertyMap);
    if (subtypes.size() == 0) {
      throw new ResourceNotFoundException("No object-subtype with values " + parsedValue[0] + " : " + parsedValue[1]);
    }

    return subtypes.get(0);
  }

}
