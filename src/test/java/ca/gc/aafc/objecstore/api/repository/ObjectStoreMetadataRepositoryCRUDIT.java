package ca.gc.aafc.objecstore.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.objectstore.api.TestConfiguration;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.respository.ObjectStoreResourceRepository;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;

public class ObjectStoreMetadataRepositoryCRUDIT extends BaseRepositoryTest {
  
  @Inject
  private ObjectStoreResourceRepository objectStoreResourceRepository;
  
  private ObjectStoreMetadata testObjectStoreMetadata;
  
  private ObjectStoreMetadata createTestObjectStoreMetadata() {
    testObjectStoreMetadata = ObjectStoreMetadataFactory.newObjectStoreMetadata().build();
    persist(testObjectStoreMetadata);
    return testObjectStoreMetadata;
  }
  
  @BeforeEach
  public void setup() { 
    createTestObjectStoreMetadata();    
  }  

  @Test
  public void findMeta_whenNoFieldsAreSelected_MetadataReturnedWithAllFields() {
    ObjectStoreMetadataDto objectStoreMetadataDto = objectStoreResourceRepository.findOne(
        testObjectStoreMetadata.getUuid(),
        new QuerySpec(ObjectStoreMetadataDto.class)
    );  
    assertNotNull(objectStoreMetadataDto);
    assertEquals(testObjectStoreMetadata.getUuid(), objectStoreMetadataDto.getUuid());
    assertEquals(testObjectStoreMetadata.getDcType(), objectStoreMetadataDto.getDcType());
    assertEquals(testObjectStoreMetadata.getAcDigitizationDate(), 
        objectStoreMetadataDto.getAcDigitizationDate());
  }

  @Test
  public void findMeta_whenManagedAttributeMapRequested_noExceptionThrown() {
    QuerySpec querySpec = new QuerySpec(ObjectStoreMetadataDto.class);
    querySpec.includeRelation(Collections.singletonList("managedAttributeMap"));

    ResourceList<ObjectStoreMetadataDto> objectStoreMetadataDto = objectStoreResourceRepository.findAll(querySpec);
    assertNotNull(objectStoreMetadataDto);
    // We cannot check for the presence of the ManagedAttributeMap in in this test, because Crnk
    // fetches relations marked with "LookupIncludeBehavior.AUTOMATICALLY_ALWAYS" outside of "findAll".
    // The test for this inclusion are done in MetadataToManagedAttributeMapRepositoryCRUDIT.
  }

  @Test
  public void create_ValidResource_ResourcePersisted() {
    ObjectStoreMetadataDto derived = new ObjectStoreMetadataDto();
    derived.setUuid(testObjectStoreMetadata.getUuid());

    ObjectStoreMetadataDto dto = new ObjectStoreMetadataDto();
    dto.setBucket(TestConfiguration.TEST_BUCKET);
    dto.setFileIdentifier(TestConfiguration.TEST_FILE_IDENTIFIER);
    dto.setAcDerivedFrom(derived);

    UUID dtoUuid = objectStoreResourceRepository.create(dto).getUuid();

    ObjectStoreMetadata result = findUnique(ObjectStoreMetadata.class, "uuid", dtoUuid);
    assertEquals(dtoUuid, result.getUuid());
    assertEquals(TestConfiguration.TEST_BUCKET, result.getBucket());
    assertEquals(TestConfiguration.TEST_FILE_IDENTIFIER, result.getFileIdentifier());
    assertEquals(derived.getUuid(), result.getAcDerivedFrom().getUuid());
  }

  @Test
  public void save_ValidResource_ResourceUpdated() {
    ObjectStoreMetadata derived = ObjectStoreMetadataFactory.newObjectStoreMetadata().build();
    persist(derived);
    ObjectStoreMetadataDto derivedDto = new ObjectStoreMetadataDto();
    derivedDto.setUuid(derived.getUuid());

    ObjectStoreMetadataDto updateMetadataDto = objectStoreResourceRepository.findOne(
      testObjectStoreMetadata.getUuid(),
      new QuerySpec(ObjectStoreMetadataDto.class));
    updateMetadataDto.setBucket(TestConfiguration.TEST_BUCKET);
    updateMetadataDto.setFileIdentifier(TestConfiguration.TEST_FILE_IDENTIFIER);
    updateMetadataDto.setAcDerivedFrom(derivedDto);

    objectStoreResourceRepository.save(updateMetadataDto);

    ObjectStoreMetadata result = findUnique(ObjectStoreMetadata.class, "uuid", updateMetadataDto.getUuid());
    assertEquals(TestConfiguration.TEST_BUCKET, result.getBucket());
    assertEquals(TestConfiguration.TEST_FILE_IDENTIFIER, result.getFileIdentifier());
    assertEquals(derived.getUuid(), result.getAcDerivedFrom().getUuid());
  }

}
