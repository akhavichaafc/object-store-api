package ca.gc.aafc.objectstore.api.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.objectstore.api.dto.MetadataManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.MetadataManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;

@Mapper(componentModel = "spring")
public interface ObjectStoreMetadataMapper {

  ObjectStoreMetadataMapper INSTANCE = Mappers.getMapper(ObjectStoreMetadataMapper.class);

  @Mapping(target = "managedAttribute", qualifiedByName="metadataManagedAttributeIdOnly")
  ObjectStoreMetadataDto toDto(ObjectStoreMetadata entity, @Context Function<String, Boolean> isAvailable);
  

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "managedAttribute", ignore = true)
  @Mapping(target = "acMetadataCreator", ignore = true)
  ObjectStoreMetadata toEntity(ObjectStoreMetadataDto dto);
  
  @InheritConfiguration
  void updateObjectStoreMetadataFromDto(ObjectStoreMetadataDto dto, @MappingTarget ObjectStoreMetadata entity);
   
  /**
   * Used to avoid cyclic reference since managedAttribute points back to ObjectStoreMetadata.
   * @param mma
   * @return
   */
  @Named("metadataManagedAttributeIdOnly")
  default List<MetadataManagedAttributeDto> metadataManagedAttributeListToMetadataManagedAttributeDtoList(List<MetadataManagedAttribute> mma, @Context Function<String, Boolean> isAvailable) {
    if (mma == null || isAvailable == null || !isAvailable.apply("managedAttribute")) {
      return null;
    }
    List<MetadataManagedAttributeDto> result = new ArrayList<>(mma.size());
    for( MetadataManagedAttribute currMma : mma) {
      // Get a builder from the current instance and set the relationships to null
      MetadataManagedAttribute mma2 = currMma.toBuilder()
          .objectStoreMetadata(null)
          .managedAttribute(null)
          .build();
      result.add(MetadataManagedAttributeMapper.INSTANCE.toDto(mma2));
    }
    return result;
  }

}
