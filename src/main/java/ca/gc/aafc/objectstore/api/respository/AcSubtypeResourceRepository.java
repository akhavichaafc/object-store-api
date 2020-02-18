package ca.gc.aafc.objectstore.api.respository;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.AcSubtypeDto;
import ca.gc.aafc.objectstore.api.entities.AcSubtype;
import ca.gc.aafc.objectstore.api.filter.RsqlFilterHandler;
import ca.gc.aafc.objectstore.api.mapper.AcSubtypeMapper;
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
public class AcSubtypeResourceRepository extends ResourceRepositoryBase<AcSubtypeDto, UUID> {

  private final BaseDAO dao;
  private final AcSubtypeMapper mapper;
  private final RsqlFilterHandler rsqlFilterHandler;

  private JpaCriteriaQueryFactory queryFactory;  

  @PostConstruct
  void setup() {
    queryFactory = dao.createWithEntityManager(JpaCriteriaQueryFactory::newInstance);
  }
  
  public AcSubtypeResourceRepository(
    BaseDAO dao,
    AcSubtypeMapper mapper,
    RsqlFilterHandler rsqlFilterHandler
  ) {
    super(AcSubtypeDto.class);
    this.dao = dao;
    this.mapper = mapper;
    this.rsqlFilterHandler = rsqlFilterHandler;
  }

  @Override
  public <S extends AcSubtypeDto> S save(S resource) {
    AcSubtypeDto dto =  (AcSubtypeDto) resource ;
    AcSubtype entity = dao.findOneByNaturalId(dto.getUuid(), AcSubtype.class);
    mapper.updateAcSubtypeFromDto(dto, entity);
    
    dao.save(entity);
    return resource;
  }

  @Override
  public AcSubtypeDto findOne(UUID uuid, QuerySpec querySpec) {
    AcSubtype acSubtype = dao.findOneByNaturalId(uuid, AcSubtype.class);
    if (acSubtype == null) {
      // Throw the 404 exception if the resource is not found.
      throw new ResourceNotFoundException(
          this.getClass().getSimpleName() + " with ID " + uuid + " Not Found.");
    }
    return mapper.toDto(acSubtype);
  }

  @Override
  public ResourceList<AcSubtypeDto> findAll(QuerySpec querySpec) {
    JpaCriteriaQuery<AcSubtype> jq = queryFactory.query(AcSubtype.class);
    
    Consumer<JpaQueryExecutor<?>> rsqlApplier = rsqlFilterHandler.getRestrictionApplier(querySpec);
    JpaQueryExecutor<AcSubtype> executor = jq.buildExecutor(querySpec);
    rsqlApplier.accept(executor);

    List<AcSubtypeDto> l = executor.getResultList().stream()
    .map( e -> mapper.toDto(e))
    .collect(Collectors.toList());
    
    return new DefaultResourceList<AcSubtypeDto>(l, null, null);
  }

  @Override
  public <S extends AcSubtypeDto> S create(S resource) {
    AcSubtypeDto dto =  (AcSubtypeDto) resource ;
    if(dto.getUuid()==null) {
      dto.setUuid(UUID.randomUUID());
    }
    
    AcSubtype entity = mapper.toEntity(dto);
    dao.save(entity);

    return resource;
  }

  @Override
  public void delete(UUID id) {
    AcSubtype acSubtype = dao.findOneByNaturalId(id, AcSubtype.class);
    if(acSubtype != null) {
      dao.delete(acSubtype);
    }
  }
  
}
