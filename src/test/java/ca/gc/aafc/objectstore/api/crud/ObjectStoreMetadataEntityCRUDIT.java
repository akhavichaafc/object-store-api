package ca.gc.aafc.objectstore.api.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class ObjectStoreMetadataEntityCRUDIT extends BaseEntityCRUDIT{


    private ObjectStoreMetadata ObjectStoreMetaUnderTest = ObjectStoreMetadataFactory.newObjectStoreMetadata().build();

    @Override
    public void testSave() {
      assertNull(ObjectStoreMetaUnderTest.getId());
      save(ObjectStoreMetaUnderTest);
      assertNotNull(ObjectStoreMetaUnderTest.getId());
    }

    @Override
    public void testFind() {
      ObjectStoreMetadata fetchedObjectStoreMeta = find(ObjectStoreMetadata.class, ObjectStoreMetaUnderTest.getId());
      assertEquals(ObjectStoreMetaUnderTest.getId(), fetchedObjectStoreMeta.getId());
    }

    @Override
    public void testRemove() {
      Integer id = ObjectStoreMetaUnderTest.getId();
      remove(ObjectStoreMetadata.class, id);
      assertNull(find(ObjectStoreMetadata.class, id));
    }

 }

