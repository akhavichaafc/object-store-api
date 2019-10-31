package ca.gc.aafc.objectstore.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.objectstore.api.dto.AgentDto;
import ca.gc.aafc.objectstore.api.entities.Agent;

@Mapper(componentModel = "spring")
public interface AgentMapper {
  AgentMapper INSTANCE = Mappers.getMapper(AgentMapper.class);  
  
  AgentDto toDto(Agent entity);
  Agent toEntity(AgentDto dto);
  
  void updateAgentFromDto(AgentDto dto, @MappingTarget Agent entity);
}
