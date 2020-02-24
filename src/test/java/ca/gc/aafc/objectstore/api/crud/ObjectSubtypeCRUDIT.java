package ca.gc.aafc.objectstore.api.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.gc.aafc.objectstore.api.entities.ObjectSubtype;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectSubtypeFactory;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class ObjectSubtypeCRUDIT extends BaseEntityCRUDIT {

  private ObjectSubtype objectSubtypeUnderTest = ObjectSubtypeFactory.newObjectSubtype()
      .acSubtype("drawing")
      .build();

  @Override
  public void testSave() {
    assertNull(objectSubtypeUnderTest.getId());
    save(objectSubtypeUnderTest);
    assertNotNull(objectSubtypeUnderTest.getId());
  }

  @Override
  public void testFind() {
    ObjectSubtype fetchedAcSubtype = find(ObjectSubtype.class,
        objectSubtypeUnderTest.getId());
    assertEquals(objectSubtypeUnderTest.getId(), fetchedAcSubtype.getId());
    assertEquals("drawing", fetchedAcSubtype.getAcSubtype());
  }

  @Override
  public void testRemove() {
    Integer id = objectSubtypeUnderTest.getId();
    remove(ObjectSubtype.class, id);
    assertNull(find(ObjectSubtype.class, id));
  }
}
