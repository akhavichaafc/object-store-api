package ca.gc.aafc.objectstore.api.mapper;

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
  ObjectStoreMetadataDto toDto(ObjectStoreMetadata entity);
  
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "managedAttribute", ignore = true)
  ObjectStoreMetadata toEntity(ObjectStoreMetadataDto dto);
  
  @Mapping(target = "managedAttribute", ignore = true)
  void updateObjectStoreMetadataFromDto(ObjectStoreMetadataDto dto, @MappingTarget ObjectStoreMetadata entity);
   
  /**
   * Used to avoid cyclic reference since managedAttribute points back to ObjectStoreMetadata.
   * @param mma
   * @return
   */
  @Named("metadataManagedAttributeIdOnly")
  default MetadataManagedAttributeDto metadataManagedAttributeToMetadataManagedAttributeDto(MetadataManagedAttribute mma) {
    if (mma == null) {
      return null;
    }
    MetadataManagedAttribute mma2 = new MetadataManagedAttribute();
    mma2.setId(mma.getId());
    mma2.setUuid(mma.getUuid());
    return MetadataManagedAttributeMapper.INSTANCE.toDto(mma2);
  }

}
