package ca.gc.aafc.objectstore.api.respository;

import java.io.IOException;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.dina.entity.SoftDeletable;
import ca.gc.aafc.dina.filter.RsqlFilterHandler;
import ca.gc.aafc.dina.filter.SimpleFilterHandler;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.repository.GoneException;
import ca.gc.aafc.dina.repository.JpaDtoRepository;
import ca.gc.aafc.dina.repository.JpaResourceRepository;
import ca.gc.aafc.dina.repository.meta.JpaMetaInformationProvider;
import ca.gc.aafc.objectstore.api.ObjectStoreConfiguration;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.DcType;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.file.FileController;
import ca.gc.aafc.objectstore.api.file.FileInformationService;
import ca.gc.aafc.objectstore.api.file.FileMetaEntry;
import ca.gc.aafc.objectstore.api.service.ObjectStoreMetadataReadService;
import io.crnk.core.exception.BadRequestException;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
@Transactional
public class ObjectStoreResourceRepository extends JpaResourceRepository<ObjectStoreMetadataDto>
    implements ObjectStoreMetadataReadService {

  public ObjectStoreResourceRepository(
    JpaDtoRepository dtoRepository,
    SimpleFilterHandler simpleFilterHandler,
    RsqlFilterHandler rsqlFilterHandler,
    JpaMetaInformationProvider metaInformationProvider,
    ObjectStoreConfiguration config,
    BaseDAO dao,
    FileInformationService fileInformationService
  ) {
    super(
      ObjectStoreMetadataDto.class,
      dtoRepository,
      Arrays.asList(simpleFilterHandler, rsqlFilterHandler),
      metaInformationProvider
    );
    this.config = config;
    this.dao = dao;
    this.fileInformationService = fileInformationService;
  }

  private final ObjectStoreConfiguration config;
  private final BaseDAO dao;
  private final FileInformationService fileInformationService;

  private static PathSpec DELETED_PATH_SPEC = PathSpec.of(SoftDeletable.DELETED_DATE_FIELD_NAME);
  private static FilterSpec DELETED_DATE_IS_NULL = new FilterSpec(DELETED_PATH_SPEC, FilterOperator.EQ, null);

  /**
   * @param resource to save
   * @return saved resource
   */
  @Override
  public <S extends ObjectStoreMetadataDto> S save(S resource) {
    S dto = super.save(resource);

    ObjectStoreMetadata entity = dao.findOneById(dto.getUuid(), ObjectStoreMetadata.class);
    handleFileRelatedData(entity);

    return (S) this.findOne(
      dto.getUuid(),
      new QuerySpec(ObjectStoreMetadataDto.class)
    );
  }

  @Override
  public ObjectStoreMetadataDto findOne(Serializable id, QuerySpec querySpec) {
    ObjectStoreMetadataDto dto = super.findOne(id, querySpec);

    if( dto.getDeletedDate() != null &&
        !querySpec.findFilter(DELETED_PATH_SPEC).isPresent() ) {
      throw new GoneException("Deleted", "ID " + id + " deleted");
    }

    return dto;
  }

  @Override
  public Optional<ObjectStoreMetadata> loadObjectStoreMetadata(UUID id) {
    return Optional.ofNullable(dao.findOneById(id, ObjectStoreMetadata.class));
  }

  @Override
  public Optional<ObjectStoreMetadata> loadObjectStoreMetadataByFileId(UUID fileId) {
    return Optional.ofNullable(dao.findOneByProperty(ObjectStoreMetadata.class, "fileIdentifier", fileId));
  }

  @Override
  public <S extends ObjectStoreMetadataDto> S create(S resource) {
    ObjectStoreMetadataDto created = super.create(resource);
    ObjectStoreMetadata entity = dao.findOneById(created.getUuid(), ObjectStoreMetadata.class);

    Function<ObjectStoreMetadata, ObjectStoreMetadata> handleFileDataFct = this::handleFileRelatedData;

    // same as assignDefaultValues(handleFileRelatedData(handleDefaultValues)) but easier to follow in my option (C.G.)
    entity = handleFileDataFct.andThen(this::assignDefaultValues)
        .apply(entity);
    
    return (S) this.findOne(
      entity.getUuid(),
      new QuerySpec(ObjectStoreMetadataDto.class)
    );
  }
  
  /**
   * Soft-delete using setDeletedDate instead of a hard delete.
   */
  @Override
  public void delete(Serializable id) {
    ObjectStoreMetadata objectStoreMetadata = dao.findOneById(id, ObjectStoreMetadata.class);
    if(objectStoreMetadata != null) {
      objectStoreMetadata.setDeletedDate(OffsetDateTime.now());
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
  
  /**
   * Method to assign default values to mandatory fields not provided at creation time.
   * If no value can be found, null with be used.
   * @param objectMetadata
   * @return the provided object with default values set (if required)
   */
  private ObjectStoreMetadata assignDefaultValues(ObjectStoreMetadata objectMetadata) {
    if (objectMetadata.getDcType() == null) {
      objectMetadata.setDcType(DcType.fromDcFormat(objectMetadata.getDcFormat()).orElse(DcType.UNDETERMINED));
    }
    
    if (objectMetadata.getXmpRightsWebStatement() == null) {
      objectMetadata.setXmpRightsWebStatement(config.getDefaultLicenceURL());
    }

    if (objectMetadata.getDcRights() == null) {
      objectMetadata.setDcRights(config.getDefaultCopyright());
    }
    
    if( objectMetadata.getXmpRightsOwner() == null) {
      objectMetadata.setXmpRightsOwner(config.getDefaultCopyrightOwner());
    }
    
    return objectMetadata;
  }

}
