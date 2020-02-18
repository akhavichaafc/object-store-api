package ca.gc.aafc.objectstore.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.objectstore.api.dto.AcSubtypeDto;
import ca.gc.aafc.objectstore.api.entities.AcSubtype;

@Mapper(componentModel = "spring")
public interface AcSubtypeMapper {
  AcSubtypeMapper INSTANCE = Mappers.getMapper(AcSubtypeMapper.class);  
  
  AcSubtypeDto toDto(AcSubtype entity);
  AcSubtype toEntity(AcSubtypeDto dto);
  
  void updateAcSubtypeFromDto(AcSubtypeDto dto, @MappingTarget AcSubtype entity);
}
