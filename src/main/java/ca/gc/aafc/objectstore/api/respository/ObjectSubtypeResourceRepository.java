package ca.gc.aafc.objectstore.api.respository;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.ObjectSubtypeDto;
import ca.gc.aafc.objectstore.api.entities.ObjectSubtype;
import ca.gc.aafc.objectstore.api.filter.RsqlFilterHandler;
import ca.gc.aafc.objectstore.api.mapper.ObjectSubtypeMapper;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.data.jpa.query.JpaQueryExecutor;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQuery;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQueryFactory;

@Repository
@Transactional
public class ObjectSubtypeResourceRepository extends ResourceRepositoryBase<ObjectSubtypeDto, UUID> {

  private final BaseDAO dao;
  private final ObjectSubtypeMapper mapper;
  private final RsqlFilterHandler rsqlFilterHandler;

  private JpaCriteriaQueryFactory queryFactory;  

  @PostConstruct
  void setup() {
    queryFactory = dao.createWithEntityManager(JpaCriteriaQueryFactory::newInstance);
  }
  
  public ObjectSubtypeResourceRepository(
    BaseDAO dao,
    ObjectSubtypeMapper mapper,
    RsqlFilterHandler rsqlFilterHandler
  ) {
    super(ObjectSubtypeDto.class);
    this.dao = dao;
    this.mapper = mapper;
    this.rsqlFilterHandler = rsqlFilterHandler;
  }

  @Override
  public <S extends ObjectSubtypeDto> S save(S resource) {
    ObjectSubtypeDto dto =  (ObjectSubtypeDto) resource ;
    ObjectSubtype entity = dao.findOneByNaturalId(dto.getUuid(), ObjectSubtype.class);
    mapper.updateObjectSubtypeFromDto(dto, entity);
    
    dao.save(entity);
    return resource;
  }

  @Override
  public ObjectSubtypeDto findOne(UUID uuid, QuerySpec querySpec) {
    ObjectSubtype objectSubtype = dao.findOneByNaturalId(uuid, ObjectSubtype.class);
    if (objectSubtype == null) {
      // Throw the 404 exception if the resource is not found.
      throw new ResourceNotFoundException(
          this.getClass().getSimpleName() + " with ID " + uuid + " Not Found.");
    }
    return mapper.toDto(objectSubtype);
  }

  @Override
  public ResourceList<ObjectSubtypeDto> findAll(QuerySpec querySpec) {
    JpaCriteriaQuery<ObjectSubtype> jq = queryFactory.query(ObjectSubtype.class);
    
    Consumer<JpaQueryExecutor<?>> rsqlApplier = rsqlFilterHandler.getRestrictionApplier(querySpec);
    JpaQueryExecutor<ObjectSubtype> executor = jq.buildExecutor(querySpec);
    rsqlApplier.accept(executor);

    List<ObjectSubtypeDto> l = executor.getResultList().stream()
    .map( e -> mapper.toDto(e))
    .collect(Collectors.toList());
    
    return new DefaultResourceList<ObjectSubtypeDto>(l, null, null);
  }

  @Override
  public <S extends ObjectSubtypeDto> S create(S resource) {
    ObjectSubtypeDto dto =  (ObjectSubtypeDto) resource ;
    if(dto.getUuid()==null) {
      dto.setUuid(UUID.randomUUID());
    }
    
    ObjectSubtype entity = mapper.toEntity(dto);
    dao.save(entity);

    return resource;
  }

  @Override
  public void delete(UUID id) {
    ObjectSubtype objectSubtype = dao.findOneByNaturalId(id, ObjectSubtype.class);
    if(objectSubtype != null) {
      dao.delete(objectSubtype);
    }
  }
  
}
