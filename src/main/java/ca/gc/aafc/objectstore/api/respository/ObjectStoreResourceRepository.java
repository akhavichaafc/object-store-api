package ca.gc.aafc.objectstore.api.respository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.file.FileInformationService;
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
public class ObjectStoreResourceRepository extends ResourceRepositoryBase<ObjectStoreMetadataDto, UUID> {

  private final BaseDAO dao;
  private final ObjectStoreMetadataMapper mapper;
  private final FileInformationService fileInformationService;
  
  private JpaCriteriaQueryFactory queryFactory;

  @Inject
  public ObjectStoreResourceRepository(BaseDAO dao, ObjectStoreMetadataMapper mapper, FileInformationService fileInformationService) {
    super(ObjectStoreMetadataDto.class);
    this.dao = dao;
    this.mapper = mapper;
    this.fileInformationService = fileInformationService;
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
    handleFileRelatedData(objectMetadata);
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

  @Override
  public <S extends ObjectStoreMetadataDto> S create(S resource) {
    ObjectStoreMetadataDto dto = (ObjectStoreMetadataDto) resource;
    if (dto.getUuid() == null) {
      dto.setUuid(UUID.randomUUID());
    }
    
    ObjectStoreMetadata objectMetadata = mapper
        .toEntity((ObjectStoreMetadataDto) resource);
    
    
    handleFileRelatedData(objectMetadata);
   
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
  
  /**
   * Triggers validation of the provided entity.
   * 
   * @param objectMetadata
   * @throws ConstraintViolationException
   */
  private void triggerValidation(ObjectStoreMetadata objectMetadata)
      throws ConstraintViolationException {
    Set<ConstraintViolation<ObjectStoreMetadata>> violations = dao.validateEntity(objectMetadata);

    if (violations.isEmpty()) {
      return;
    }
    throw new ConstraintViolationException(violations);
  }
  
  /**
   * Method responsible for making sure that the bucket and fileIdentifier exist.
   * 
   * @param objectMetadata
   * @throws ValidationException
   */
  private void handleFileRelatedData(ObjectStoreMetadata objectMetadata)
      throws ValidationException {
    // we need to validate at least that bucket name and fileIdentifier are there
    triggerValidation(objectMetadata);

    // how do we get the bucket from here?
    if (!fileInformationService.isFileWithPrefixExists(objectMetadata.getBucket(),
        objectMetadata.getFileIdentifier().toString())) {
      throw new ValidationException(
          "fileIdentifier: " + objectMetadata.getFileIdentifier().toString()
              + " could not be found in bucket: " + objectMetadata.getBucket());
    }
  }
}
