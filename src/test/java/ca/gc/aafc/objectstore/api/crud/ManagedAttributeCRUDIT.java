package ca.gc.aafc.objectstore.api.crud;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;

public class ManagedAttributeCRUDIT extends BaseEntityCRUDIT {

  private ManagedAttribute managedAttributeUnderTest = ManagedAttributeFactory.newManagedAttribute()
      .acceptedValues(new String[] {"a", "b"})
      .build();

  @Override
  public void testSave() {
    assertNull(managedAttributeUnderTest.getId());
    save(managedAttributeUnderTest);
    assertNotNull(managedAttributeUnderTest.getId());
  }

  @Override
  public void testFind() {
    ManagedAttribute fetchedObjectStoreMeta = find(ManagedAttribute.class,
        managedAttributeUnderTest.getId());
    assertEquals(managedAttributeUnderTest.getId(), fetchedObjectStoreMeta.getId());
    
    assertArrayEquals(new String[] {"a", "b"}, managedAttributeUnderTest.getAcceptedValues());
  }

  @Override
  public void testRemove() {
    Integer id = managedAttributeUnderTest.getId();
    remove(ManagedAttribute.class, id);
    assertNull(find(ManagedAttribute.class, id));
  }
}
