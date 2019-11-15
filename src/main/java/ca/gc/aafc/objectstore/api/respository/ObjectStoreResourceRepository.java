package ca.gc.aafc.objectstore.api.respository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
import ca.gc.aafc.objectstore.api.entities.Agent;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.file.FileController;
import ca.gc.aafc.objectstore.api.file.FileInformationService;
import ca.gc.aafc.objectstore.api.file.FileObjectInfo;
import ca.gc.aafc.objectstore.api.mapper.ObjectStoreMetadataMapper;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQuery;
import io.crnk.data.jpa.query.criteria.JpaCriteriaQueryFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    
    // relationships
    if (resource.getAcMetadataCreator() != null) {
      objectMetadata.setAcMetadataCreator(
          dao.getReferenceByNaturalId(Agent.class, resource.getAcMetadataCreator().getUuid()));
    }
    
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
    return mapper.toDto(objectStoreMetadata, fieldName -> dao.isLoaded(objectStoreMetadata, fieldName));
  }

  @Override
  public ResourceList<ObjectStoreMetadataDto> findAll(QuerySpec querySpec) {
    JpaCriteriaQuery<ObjectStoreMetadata> jq = queryFactory.query(ObjectStoreMetadata.class);
   
    List<ObjectStoreMetadataDto> l = jq.buildExecutor(querySpec).getResultList().stream()
    .map( objectStoreMetadata -> mapper.toDto(objectStoreMetadata, fieldName -> dao.isLoaded(objectStoreMetadata, fieldName)))
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
   
    // relationships
    if (resource.getAcMetadataCreator() != null) {
      objectMetadata.setAcMetadataCreator(
          dao.getReferenceByNaturalId(Agent.class, resource.getAcMetadataCreator().getUuid()));
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
   * Method responsible for dealing with validation and setting of data related to 
   * files.
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
    
    try {
      Optional<String> possibleFileName = fileInformationService.getFileNameByPrefix(
          objectMetadata.getBucket(), objectMetadata.getFileIdentifier().toString());
      Optional<FileObjectInfo> fileObjectInfo = fileInformationService
          .getFileInfo(possibleFileName.orElse(""), objectMetadata.getBucket());
      
      objectMetadata.setOriginalFilename(fileObjectInfo.map(foi -> foi
          .extractHeader(FileObjectInfo.CUSTOM_HEADER_PREFIX + FileController.HEADER_ORIGINAL_FILENAME)
          .get(0)).orElse("?"));
      
      objectMetadata.setDcFormat(fileObjectInfo.map(foi -> foi
          .extractHeader(FileObjectInfo.CUSTOM_HEADER_PREFIX + FileController.MEDIA_TYPE)
          .get(0)).orElse("?"));
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
