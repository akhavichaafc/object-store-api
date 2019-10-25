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
public interface MetadataManagedAttributeMapper {
  
  MetadataManagedAttributeMapper INSTANCE = Mappers.getMapper(MetadataManagedAttributeMapper.class);

  /**
   * objectStoreMetadata.managedAttribute property will be ignore to avoid circular dependency
   * 
   * @param dto
   * @return
   */
  @Mapping(target = "objectStoreMetadata", qualifiedByName = "objectStoreMetadataIdOnly")
  MetadataManagedAttributeDto toDto(MetadataManagedAttribute entity);

  @Mapping(target = "objectStoreMetadata", ignore = true)
  @Mapping(target = "managedAttribute", ignore = true)
  MetadataManagedAttribute toEntity(MetadataManagedAttributeDto dto);

  @Mapping(target = "objectStoreMetadata", ignore = true)
  @Mapping(target = "managedAttribute", ignore = true)
  void updateMetadataManagedAttributeFromDto(MetadataManagedAttributeDto dto,
      @MappingTarget MetadataManagedAttribute entity);

  /**
   * Used to avoid cyclic reference since objectStoreMetadata points back to
   * MetadataManagedAttribute.
   * 
   * @param osm
   * @return
   */
  @Named("objectStoreMetadataIdOnly")
  default ObjectStoreMetadataDto objectStoreMetadataToObjectStoreMetadataDto(ObjectStoreMetadata osm) {
    if (osm == null) {
      return null;
    }
    
    // Get a builder from the current instance and set the relationships to null
    ObjectStoreMetadata osm2 = osm.toBuilder()
        .managedAttribute(null)
        .build();
    return ObjectStoreMetadataMapper.INSTANCE.toDto(osm2, null);
  }
  
}
