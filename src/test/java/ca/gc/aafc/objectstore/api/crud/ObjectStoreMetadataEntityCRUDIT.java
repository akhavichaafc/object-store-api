package ca.gc.aafc.objectstore.api.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.objectstore.api.entities.Agent;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.MetadataManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.testsupport.factories.AgentFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.MetadataManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class ObjectStoreMetadataEntityCRUDIT extends BaseEntityCRUDIT {

  private static final ZoneId MTL_TZ = ZoneId.of("America/Montreal");
  private final ZonedDateTime TEST_ZONED_DT = ZonedDateTime.of(2019, 1, 2, 3, 4, 5, 0, MTL_TZ);
  private final OffsetDateTime TEST_OFFSET_DT = TEST_ZONED_DT.toOffsetDateTime();

  private ObjectStoreMetadata objectStoreMetaUnderTest = ObjectStoreMetadataFactory
      .newObjectStoreMetadata()
      .acDigitizationDate(TEST_OFFSET_DT)
      .build();

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
    
    //should be auto-generated
    assertNotNull(fetchedObjectStoreMeta.getCreatedDate());
    assertNotNull(fetchedObjectStoreMeta.getXmpMetadataDate());
  }

  @Override
  public void testRemove() {
    Integer id = objectStoreMetaUnderTest.getId();
    remove(ObjectStoreMetadata.class, id);
    assertNull(find(ObjectStoreMetadata.class, id));
  }
  
  @Test
  public void testRelationships() {
    ManagedAttribute ma = ManagedAttributeFactory.newManagedAttribute().build();
    save(ma, false);

    ObjectStoreMetadata derivedFrom = ObjectStoreMetadataFactory.newObjectStoreMetadata()
        .fileIdentifier(UUID.randomUUID())
        .build();
    save(derivedFrom);

    Agent metatdataCreator = AgentFactory.newAgent().build();
    save(metatdataCreator, false);
    assertNotNull(metatdataCreator.getId());
   
    ObjectStoreMetadata osm = ObjectStoreMetadataFactory
        .newObjectStoreMetadata()
        .acMetadataCreator(metatdataCreator)
        .acDigitizationDate(TEST_OFFSET_DT)
        .acDerivedFrom(derivedFrom)
        .dcCreator(metatdataCreator).build();
    save(osm, false);
    assertNotNull(osm.getId());
    
    // link the 2 entities
    MetadataManagedAttribute mma = MetadataManagedAttributeFactory.newMetadataManagedAttribute()
    .objectStoreMetadata(osm)
    .managedAttribute(ma)
    .assignedValue("test value")
    .build();
    
    save(mma);
    
    MetadataManagedAttribute restoredMma = find(MetadataManagedAttribute.class, mma.getId());
    assertEquals(osm.getId(), restoredMma.getObjectStoreMetadata().getId());
    
    ObjectStoreMetadata restoredOsm = find(ObjectStoreMetadata.class, osm.getId());
    assertEquals(metatdataCreator.getId(), restoredOsm.getAcMetadataCreator().getId());
    assertEquals(derivedFrom.getId(), restoredOsm.getAcDerivedFrom().getId());
    assertEquals(metatdataCreator.getId(), restoredOsm.getDcCreator().getId());
  }

}
