package ca.gc.aafc.objectstore.api.respository;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.filter.RsqlFilterHandler;
import ca.gc.aafc.objectstore.api.interfaces.SoftDeletableRepository;
import ca.gc.aafc.objectstore.api.mapper.ManagedAttributeMapper;
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
public class ManagedAttributeResourceRepository extends ResourceRepositoryBase<ManagedAttributeDto, UUID> {

  private final BaseDAO dao;
  private final ManagedAttributeMapper mapper;
  private final RsqlFilterHandler rsqlFilterHandler;

  private JpaCriteriaQueryFactory queryFactory;  

  @PostConstruct
  void setup() {
    queryFactory = dao.createWithEntityManager(JpaCriteriaQueryFactory::newInstance);
  }

  public ManagedAttributeResourceRepository(
    BaseDAO dao,
    ManagedAttributeMapper mapper,
    RsqlFilterHandler rsqlFilterHandler
  ) {
    super(ManagedAttributeDto.class);
    this.dao = dao;
    this.mapper = mapper;
    this.rsqlFilterHandler = rsqlFilterHandler;
  }

  /**
   * @param resource
   *          to save
   * @return saved resource
   */
  @Override
  public <S extends ManagedAttributeDto> S save(S resource) {
    ManagedAttributeDto dto = (ManagedAttributeDto) resource;
    ManagedAttribute managedAttribute = dao.findOneByNaturalId(dto.getUuid(), ManagedAttribute.class);
    mapper.updateManagedAttributeFromDto(dto, managedAttribute);
    dao.save(managedAttribute);
    return resource;
  }

  @Override
  public ManagedAttributeDto findOne(UUID uuid, QuerySpec querySpec) {
    ManagedAttribute managedAttribute = dao.findOneByNaturalId(uuid, ManagedAttribute.class);
    if (managedAttribute == null) {
      // Throw the 404 exception if the resource is not found.
      throw new ResourceNotFoundException(
          this.getClass().getSimpleName() + " with ID " + uuid + " Not Found.");
    }

    if (managedAttribute.getDeletedDate() != null
        && !querySpec.findFilter(SoftDeletableRepository.DELETED_PATH_SPEC).isPresent()) {
      // Throw the 410 exception if the resource is not found.
      throw new GoneException("ID " + uuid + " deleted");
    }

    return mapper.toDto(managedAttribute);
  }

  @Override
  public ResourceList<ManagedAttributeDto> findAll(QuerySpec querySpec) {
    JpaCriteriaQuery<ManagedAttribute> jq = queryFactory.query(ManagedAttribute.class);

    if (!querySpec.findFilter(SoftDeletableRepository.DELETED_PATH_SPEC).isPresent()) {
      querySpec.addFilter(SoftDeletableRepository.DELETED_DATE_IS_NULL);
    }

    Consumer<JpaQueryExecutor<?>> rsqlApplier = rsqlFilterHandler.getRestrictionApplier(querySpec);
    JpaQueryExecutor<ManagedAttribute> executor = jq.buildExecutor(querySpec);
    rsqlApplier.accept(executor);

    List<ManagedAttributeDto> l = executor.getResultList().stream()
    .map(mapper::toDto)
    .collect(Collectors.toList());
    
    return new DefaultResourceList<ManagedAttributeDto>(l, null, null);
  }
  
  public ResourceList<ManagedAttributeDto> findAll(Iterable<Serializable> ids) {
    @SuppressWarnings("unchecked")
    List<ManagedAttributeDto> managedAttributes = new ArrayList<ManagedAttributeDto>();
    Iterator it = ids.iterator();
    while(it.hasNext()) {
      managedAttributes.add(findOne((UUID)it.next(),null));      
    }
    return new DefaultResourceList<ManagedAttributeDto>(managedAttributes, null, null);
  }  

  @Override
  public <S extends ManagedAttributeDto> S create(S resource) {
    ManagedAttributeDto dto = (ManagedAttributeDto) resource;
    if (dto.getUuid() == null) {
      dto.setUuid(UUID.randomUUID());
    }
    ManagedAttribute managedAttribute = mapper.toEntity((ManagedAttributeDto) resource);
    dao.save(managedAttribute);
    return resource;
  }

  @Override
  public void delete(UUID id) {
    ManagedAttribute managedAttribute = dao.findOneByNaturalId(id, ManagedAttribute.class);
    if (managedAttribute != null) {
      managedAttribute.setDeletedDate(OffsetDateTime.now());
    }
  }
}
