package ca.gc.aafc.objectstore.api.respository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.MetadataManagedAttributeDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.MetadataManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.mapper.MetadataManagedAttributeMapper;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQuery;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQueryFactory;
import lombok.extern.slf4j.Slf4j;

@Repository
@Transactional
@Slf4j
public class MetadataManagedAttributeRepository extends ResourceRepositoryBase<MetadataManagedAttributeDto, UUID> {

  private final BaseDAO dao;
  private final MetadataManagedAttributeMapper mapper;
  
  private JpaCriteriaQueryFactory queryFactory;

  @Inject
  public MetadataManagedAttributeRepository(BaseDAO dao, MetadataManagedAttributeMapper mapper) {
    super(MetadataManagedAttributeDto.class);
    this.dao = dao;
    this.mapper = mapper;
  }
  
  @PostConstruct
  void setup() {
    queryFactory = dao.createWithEntityManager(JpaCriteriaQueryFactory::newInstance);
  }

  /**
   * @param resource
   *          to save
   * @return saved resource
   */
  @Override
  public <S extends MetadataManagedAttributeDto> S save(S resource) {
    MetadataManagedAttributeDto dto =  (MetadataManagedAttributeDto) resource ;
    MetadataManagedAttribute metadataManagedAttribute = dao.findOneByNaturalId(dto.getUuid(), MetadataManagedAttribute.class);
    mapper.updateMetadataManagedAttributeFromDto(dto, metadataManagedAttribute);
    
    System.out.println("Saving current objectStoreMetadata:" + metadataManagedAttribute.getObjectStoreMetadata());
    dao.save(metadataManagedAttribute);
    return resource;
  }

  @Override
  public MetadataManagedAttributeDto findOne(UUID uuid, QuerySpec querySpec) {
    MetadataManagedAttribute metadataManagedAttribute = dao.findOneByNaturalId(uuid, MetadataManagedAttribute.class);
    if(metadataManagedAttribute ==null){
    // Throw the 404 exception if the resource is not found.
      throw new ResourceNotFoundException(
          this.getClass().getSimpleName() + " with ID " + uuid + " Not Found."
      );
    }
    return mapper.toDto(metadataManagedAttribute);
  }

  @Override
  public ResourceList<MetadataManagedAttributeDto> findAll(QuerySpec querySpec) {
    JpaCriteriaQuery<MetadataManagedAttribute> jq = queryFactory.query(MetadataManagedAttribute.class);
    
    List<MetadataManagedAttributeDto> l = jq.buildExecutor(querySpec).getResultList().stream()
    .map( e -> mapper.toDto(e))
    .collect(Collectors.toList());
    
    return new DefaultResourceList<MetadataManagedAttributeDto>(l, null, null);
  }

  @Override
  public <S extends MetadataManagedAttributeDto> S create(S resource) {
    MetadataManagedAttributeDto dto =  (MetadataManagedAttributeDto) resource ;
    if(dto.getUuid()==null) {
      dto.setUuid(UUID.randomUUID());
    }
    
    MetadataManagedAttribute metadataManagedAttribute = mapper
        .toEntity((MetadataManagedAttributeDto) resource);
    
    log.info("Creating MetadataManagedAttributeDto. received dto:" + metadataManagedAttribute);

    // relationships
    if (dto.getManagedAttribute() != null) {
      metadataManagedAttribute.setManagedAttribute(dao.getReferenceByNaturalId(ManagedAttribute.class, dto.getManagedAttribute().getUuid()));
    }
    
    if(dto.getObjectStoreMetadata() != null) {
      metadataManagedAttribute.setObjectStoreMetadata(dao.getReferenceByNaturalId(ObjectStoreMetadata.class, dto.getObjectStoreMetadata().getUuid()));
    }

    dao.save(metadataManagedAttribute);

    return resource;
  }
  
  @Override
  public void delete(UUID id) {
    MetadataManagedAttribute objectStoreMetadata = dao.findOneByNaturalId(id, MetadataManagedAttribute.class);
    if(objectStoreMetadata != null) {
      dao.delete(objectStoreMetadata);
    }
  }
}
