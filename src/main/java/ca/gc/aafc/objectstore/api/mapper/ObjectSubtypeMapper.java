package ca.gc.aafc.objectstore.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.objectstore.api.dto.ObjectSubtypeDto;
import ca.gc.aafc.objectstore.api.entities.ObjectSubtype;

@Mapper(componentModel = "spring")
public interface ObjectSubtypeMapper {
  ObjectSubtypeMapper INSTANCE = Mappers.getMapper(ObjectSubtypeMapper.class);  
  
  ObjectSubtypeDto toDto(ObjectSubtype entity);
  ObjectSubtype toEntity(ObjectSubtypeDto dto);
  
  void updateObjectSubtypeFromDto(ObjectSubtypeDto dto, @MappingTarget ObjectSubtype entity);
}
