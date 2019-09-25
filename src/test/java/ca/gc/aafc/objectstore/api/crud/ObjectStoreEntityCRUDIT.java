package ca.gc.aafc.objectstore.api.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMeta;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetaFactory;

public class ObjectStoreEntityCRUDIT extends BaseEntityCRUDIT{


    private ObjectStoreMeta ObjectStoreMetaUnderTest = ObjectStoreMetaFactory.newObjectStoreMeta().build();

    @Override
    public void testSave() {
      assertNull(ObjectStoreMetaUnderTest.getId());
      save(ObjectStoreMetaUnderTest);
      assertNotNull(ObjectStoreMetaUnderTest.getId());
    }

    @Override
    public void testFind() {
      ObjectStoreMeta fetchedObjectStoreMeta = find(ObjectStoreMeta.class, ObjectStoreMetaUnderTest.getId());
      assertEquals(ObjectStoreMetaUnderTest.getId(), fetchedObjectStoreMeta.getId());
    }

    @Override
    public void testRemove() {
      Integer id = ObjectStoreMetaUnderTest.getId();
      remove(ObjectStoreMeta.class, id);
      assertNull(find(ObjectStoreMeta.class, id));
    }

 }

