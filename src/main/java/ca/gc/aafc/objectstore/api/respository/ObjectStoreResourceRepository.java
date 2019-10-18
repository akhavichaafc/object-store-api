package ca.gc.aafc.objectstore.api.respository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.mapper.ObjectStoreMetadataMapper;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQuery;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQueryFactory;

@Repository
@Transactional
public class ObjectStoreResourceRepository
    extends ResourceRepositoryBase<ObjectStoreMetadataDto, UUID> {

  private final BaseDAO dao;
  private final ObjectStoreMetadataMapper mapper;
  
  private JpaCriteriaQueryFactory queryFactory;

  @Inject
  public ObjectStoreResourceRepository(BaseDAO dao, ObjectStoreMetadataMapper mapper) {
    super(ObjectStoreMetadataDto.class);
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
  public <S extends ObjectStoreMetadataDto> S save(S resource) {
    ObjectStoreMetadataDto dto =  (ObjectStoreMetadataDto) resource ;
    ObjectStoreMetadata objectMetadata = dao.findOneByNaturalId(dto.getUuid(), ObjectStoreMetadata.class);
    mapper.updateObjectStoreMetadataFromDto(dto, objectMetadata);
    dao.save(objectMetadata);
    return resource;
  }

  @Override
  public ObjectStoreMetadataDto findOne(UUID uuid, QuerySpec querySpec) {
    ObjectStoreMetadata objectStoreMetadata = dao.findOneByNaturalId(uuid, ObjectStoreMetadata.class);
    if(objectStoreMetadata ==null){
    // Throw the 404 exception if the resource is not found.
      throw new ResourceNotFoundException(
          this.getClass().getSimpleName() + " with ID " + uuid + " Not Found."
      );
    }
    return mapper.toDto(objectStoreMetadata);
  }

  @Override
  public ResourceList<ObjectStoreMetadataDto> findAll(QuerySpec querySpec) {
    JpaCriteriaQuery<ObjectStoreMetadata> jq = queryFactory.query(ObjectStoreMetadata.class);
    
    List<ObjectStoreMetadataDto> l = jq.buildExecutor(querySpec).getResultList().stream()
    .map(mapper::toDto)
    .collect(Collectors.toList());
    
    return new DefaultResourceList<ObjectStoreMetadataDto>(l, null, null);
  }
  
  public void setRelationships(UUID objectStoreMetadataNaturalKey, List<UUID> targetIds) {
    ObjectStoreMetadata objectMetadata = dao.findOneByNaturalId(objectStoreMetadataNaturalKey, ObjectStoreMetadata.class);
    objectMetadata.getManagedAttributes().clear();
    for(UUID rel : targetIds) {
      if (rel != null) {
        objectMetadata.getManagedAttributes().add(dao.getReferenceByNaturalId(ManagedAttribute.class, rel));
      }
    }
    dao.save(objectMetadata);
  }
  
  public void addRelationships(UUID objectStoreMetadataNaturalKey, List<UUID> targetIds) {
    ObjectStoreMetadata objectMetadata = dao.findOneByNaturalId(objectStoreMetadataNaturalKey, ObjectStoreMetadata.class);
    for(UUID rel : targetIds) {
      if (rel != null) {
        objectMetadata.getManagedAttributes().add(dao.getReferenceByNaturalId(ManagedAttribute.class, rel));
      }
    }
    dao.save(objectMetadata);
  }
  
  public void removeRelationships(UUID objectStoreMetadataNaturalKey, List<UUID> targetIds) {
    ObjectStoreMetadata objectMetadata = dao.findOneByNaturalId(objectStoreMetadataNaturalKey, ObjectStoreMetadata.class);
    objectMetadata.getManagedAttributes().removeIf( (ma) -> targetIds.contains(ma.getUuid()));
    dao.save(objectMetadata);
  }

  @Override
  public <S extends ObjectStoreMetadataDto> S create(S resource) {
    ObjectStoreMetadataDto dto =  (ObjectStoreMetadataDto) resource ;
    if(dto.getUuid()==null) {
      dto.setUuid(UUID.randomUUID());
    }
    
    ObjectStoreMetadata objectMetadata = mapper
        .toEntity((ObjectStoreMetadataDto) resource);

    // relationships
    if (resource.getManagedAttributes() != null) {
      objectMetadata.setManagedAttributes(new ArrayList<ManagedAttribute>());
      for (ManagedAttributeDto mdto : resource.getManagedAttributes()) {
        objectMetadata.getManagedAttributes().add(dao.getReferenceByNaturalId(ManagedAttribute.class, mdto.getUuid()));
      }
    }

    dao.save(objectMetadata);

    return resource;
  }
  
  @Override
  public void delete(UUID id) {
    ObjectStoreMetadata objectStoreMetadata = dao.findOneByNaturalId(id, ObjectStoreMetadata.class);
    if(objectStoreMetadata != null) {
      dao.delete(objectStoreMetadata);
    }
  }
}
