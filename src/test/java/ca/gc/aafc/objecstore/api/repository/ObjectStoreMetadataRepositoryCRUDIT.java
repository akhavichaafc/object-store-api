package ca.gc.aafc.objecstore.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.respository.ObjectStoreResourceRepository;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;
import io.crnk.core.queryspec.QuerySpec;

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
    
}
