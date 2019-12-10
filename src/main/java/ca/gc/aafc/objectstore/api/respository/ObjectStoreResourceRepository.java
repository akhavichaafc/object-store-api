package ca.gc.aafc.objectstore.api.respository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.Agent;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.file.FileController;
import ca.gc.aafc.objectstore.api.file.FileInformationService;
import ca.gc.aafc.objectstore.api.file.FileMetaEntry;
import ca.gc.aafc.objectstore.api.mapper.ObjectStoreMetadataMapper;
import ca.gc.aafc.objectstore.api.service.ObjectStoreMetadataReadService;
import io.crnk.core.exception.BadRequestException;
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
public class ObjectStoreResourceRepository extends ResourceRepositoryBase<ObjectStoreMetadataDto, UUID> implements ObjectStoreMetadataReadService {

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

    objectMetadata = handleFileRelatedData(objectMetadata);
    
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
    ObjectStoreMetadata objectStoreMetadata = loadObjectStoreMetadata(uuid).orElseThrow( () -> new ResourceNotFoundException(
          this.getClass().getSimpleName() + " with ID " + uuid + " Not Found."));
    return mapper.toDto(objectStoreMetadata, fieldName -> dao.isLoaded(objectStoreMetadata, fieldName));
  }
  
  @Override
  public Optional<ObjectStoreMetadata> loadObjectStoreMetadata(UUID id) {
    return Optional.ofNullable(dao.findOneByNaturalId(id, ObjectStoreMetadata.class));
  }
  
  @Override
  public Optional<ObjectStoreMetadata> loadObjectStoreMetadataByFileId(UUID fileId) {
    return Optional.ofNullable(dao.findOneByProperty(ObjectStoreMetadata.class, "fileIdentifier", fileId));
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
    
    objectMetadata = handleFileRelatedData(objectMetadata);
   
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
   * Method responsible for dealing with validation and setting of data related to 
   * files.
   * 
   * @param objectMetadata
   * @throws ValidationException
   */
  private ObjectStoreMetadata handleFileRelatedData(ObjectStoreMetadata objectMetadata)
      throws ValidationException {
    // we need to validate at least that bucket name and fileIdentifier are there
    if (StringUtils.isBlank(objectMetadata.getBucket())
        || StringUtils.isBlank(Objects.toString(objectMetadata.getFileIdentifier(), ""))) {
      throw new ValidationException("fileIdentifier and bucket should be provided");
    }

    try {
      FileMetaEntry fileMetaEntry = fileInformationService.getJsonFileContentAs(
          objectMetadata.getBucket(),
          objectMetadata.getFileIdentifier().toString() + FileMetaEntry.SUFFIX,
          FileMetaEntry.class).orElseThrow( () -> new BadRequestException(
              this.getClass().getSimpleName() + " with ID " + objectMetadata.getFileIdentifier() + " Not Found."));

      objectMetadata.setFileExtension(fileMetaEntry.getEvaluatedFileExtension());
      objectMetadata.setOriginalFilename(fileMetaEntry.getOriginalFilename());
      objectMetadata.setDcFormat(fileMetaEntry.getDetectedMediaType());
      objectMetadata.setAcHashValue(fileMetaEntry.getSha1Hex());
      objectMetadata.setAcHashFunction(FileController.DIGEST_ALGORITHM);

      return objectMetadata;

    } catch (IOException e) {
      log.error(e.getMessage());
      throw new BadRequestException("Can't process " + objectMetadata.getFileIdentifier());
    }

  }

}
