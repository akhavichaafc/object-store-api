package ca.gc.aafc.objectstore.api.respository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.AgentDto;
import ca.gc.aafc.objectstore.api.entities.Agent;
import ca.gc.aafc.objectstore.api.mapper.AgentMapper;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQuery;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQueryFactory;

@Repository
@Transactional
public class AgentResourceRepository extends ResourceRepositoryBase<AgentDto, UUID> {

  private final BaseDAO dao;
  private final AgentMapper mapper;

  private JpaCriteriaQueryFactory queryFactory;  

  @PostConstruct
  void setup() {
    queryFactory = dao.createWithEntityManager(JpaCriteriaQueryFactory::newInstance);
  }
  
  public AgentResourceRepository(BaseDAO dao, AgentMapper mapper) {
    super(AgentDto.class);
    this.dao = dao;
    this.mapper = mapper;
  }

  @Override
  public <S extends AgentDto> S save(S resource) {
    AgentDto dto =  (AgentDto) resource ;
    Agent entity = dao.findOneByNaturalId(dto.getUuid(), Agent.class);
    mapper.updateAgentFromDto(dto, entity);
    
    dao.save(entity);
    return resource;
  }

  @Override
  public AgentDto findOne(UUID uuid, QuerySpec querySpec) {
    Agent agent = dao.findOneByNaturalId(uuid, Agent.class);
    if (agent == null) {
      // Throw the 404 exception if the resource is not found.
      throw new ResourceNotFoundException(
          this.getClass().getSimpleName() + " with ID " + uuid + " Not Found.");
    }
    return mapper.toDto(agent);
  }

  @Override
  public ResourceList<AgentDto> findAll(QuerySpec querySpec) {
    JpaCriteriaQuery<Agent> jq = queryFactory.query(Agent.class);
    
    List<AgentDto> l = jq.buildExecutor(querySpec).getResultList().stream()
    .map( e -> mapper.toDto(e))
    .collect(Collectors.toList());
    
    return new DefaultResourceList<AgentDto>(l, null, null);
  }

  @Override
  public <S extends AgentDto> S create(S resource) {
    AgentDto dto =  (AgentDto) resource ;
    if(dto.getUuid()==null) {
      dto.setUuid(UUID.randomUUID());
    }
    
    Agent entity = mapper.toEntity(dto);
    dao.save(entity);

    return resource;
  }
  
  @Override
  public void delete(UUID id) {
    Agent agent = dao.findOneByNaturalId(id, Agent.class);
    if(agent != null) {
      dao.delete(agent);
    }
  }
  
}
