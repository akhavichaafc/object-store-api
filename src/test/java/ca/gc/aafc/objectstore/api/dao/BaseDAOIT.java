package ca.gc.aafc.objectstore.api.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.objectstore.api.BaseIntegrationTest;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class BaseDAOIT extends BaseIntegrationTest {
  
  @Inject
  private BaseDAO dao;
  
  @Test
  public void testGivenNewEntity_findByNaturalId() {
    UUID naturalKey = UUID.randomUUID();
    ObjectStoreMetadata osm = ObjectStoreMetadataFactory.newObjectStoreMetadata()
        .uuid(naturalKey)
        .build();
    dao.save(osm);
    
    // detached the object to make sure we don't get the entity from memory
    detach(osm);
    
    ObjectStoreMetadata osm2 = dao.findOneByNaturalId(naturalKey, ObjectStoreMetadata.class);
    assertNotNull(osm2);
  }
  
  @Test
  public void testGivenNewEntity_existsByNaturalId() {
    UUID naturalKey = UUID.randomUUID();
    ObjectStoreMetadata osm = ObjectStoreMetadataFactory.newObjectStoreMetadata()
        .uuid(naturalKey)
        .build();
    dao.save(osm);
    
    // detached the object to make sure we don't get the entity from memory
    detach(osm);
    
    assertFalse(dao.existsByNaturalId(UUID.randomUUID(), ObjectStoreMetadata.class));
    assertTrue(dao.existsByNaturalId(naturalKey, ObjectStoreMetadata.class));
  }
  
/* TODO enable when Issue 17672 will be done 
 * @Test 
  public void testGivenNewEntity_setRelationshipBy_Reference() {
    UUID managedAttributeNaturalKey = UUID.randomUUID();
    UUID osmNaturalKey = UUID.randomUUID();
    ManagedAttribute ma = ManagedAttributeFactory.newManagedAttribute()
        .uuid(managedAttributeNaturalKey).build();
    dao.save(ma);
    
    ObjectStoreMetadata osm = ObjectStoreMetadataFactory.newObjectStoreMetadata()
        .uuid(osmNaturalKey)
        .build();
    dao.save(osm);
    
    // detached the object to make sure we don't get the entity from memory
    entityManager.detach(ma);
    entityManager.detach(osm);
    
    ObjectStoreMetadata osm2 = dao.findOneByNaturalId(osmNaturalKey, ObjectStoreMetadata.class);

    dao.setRelationshipUsing(ManagedAttribute.class, managedAttributeNaturalKey, objConsumer);
    assertNotNull(osm2);
  }*/

}
