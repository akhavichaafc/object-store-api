package ca.gc.aafc.objectstore.api.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.google.api.client.util.Lists;

import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class ObjectStoreMetadataEntityCRUDIT extends BaseEntityCRUDIT {

  private static final ZoneId MTL_TZ = ZoneId.of("America/Montreal");
  private final ZonedDateTime TEST_ZONED_DT = ZonedDateTime.of(2019, 1, 2, 3, 4, 5, 0, MTL_TZ);
  private final OffsetDateTime TEST_OFFSET_DT = TEST_ZONED_DT.toOffsetDateTime();

  private ObjectStoreMetadata objectStoreMetaUnderTest = ObjectStoreMetadataFactory
      .newObjectStoreMetadata().acDigitizationDate(TEST_OFFSET_DT).build();

  @Override
  public void testSave() {
    assertNull(objectStoreMetaUnderTest.getId());
    save(objectStoreMetaUnderTest);
    assertNotNull(objectStoreMetaUnderTest.getId());
  }

  @Override
  public void testFind() {
    ObjectStoreMetadata fetchedObjectStoreMeta = find(ObjectStoreMetadata.class,
        objectStoreMetaUnderTest.getId());
    assertEquals(objectStoreMetaUnderTest.getId(), fetchedObjectStoreMeta.getId());
    
    // the returned acDigitizationDate will use the timezone of the server
    assertEquals(objectStoreMetaUnderTest.getAcDigitizationDate(),
        fetchedObjectStoreMeta.getAcDigitizationDate()
        .atZoneSameInstant(MTL_TZ)
        .toOffsetDateTime());
  }

  @Override
  public void testRemove() {
    Integer id = objectStoreMetaUnderTest.getId();
    remove(ObjectStoreMetadata.class, id);
    assertNull(find(ObjectStoreMetadata.class, id));
  }
  
  @Test
  public void testLinks() {
    ManagedAttribute ma = ManagedAttributeFactory.newManagedAttribute().build();
    save(ma);
    ObjectStoreMetadata a = objectStoreMetaUnderTest = ObjectStoreMetadataFactory
        .newObjectStoreMetadata().acDigitizationDate(TEST_OFFSET_DT)
        .managedAttributes(Collections.singletonList(ma)).build();
    save(a);
    assertNotNull(objectStoreMetaUnderTest.getId());
    
  }

}
