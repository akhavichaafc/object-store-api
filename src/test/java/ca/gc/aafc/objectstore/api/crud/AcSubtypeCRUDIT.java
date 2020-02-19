package ca.gc.aafc.objectstore.api.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.gc.aafc.objectstore.api.entities.AcSubtype;
import ca.gc.aafc.objectstore.api.testsupport.factories.AcSubtypeFactory;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class AcSubtypeCRUDIT extends BaseEntityCRUDIT {

  private static String RANDOM_NAME = TestableEntityFactory.generateRandomNameLettersOnly(10);
  
  private AcSubtype acSubtypeUnderTest = AcSubtypeFactory.newAcSubtype()
      .acSubtype("drawing")
      .build();

  @Override
  public void testSave() {
    assertNull(acSubtypeUnderTest.getId());
    save(acSubtypeUnderTest);
    assertNotNull(acSubtypeUnderTest.getId());
  }

  @Override
  public void testFind() {
    AcSubtype fetchedAcSubtype = find(AcSubtype.class,
        acSubtypeUnderTest.getId());
    assertEquals(acSubtypeUnderTest.getId(), fetchedAcSubtype.getId());
    assertEquals("drawing", fetchedAcSubtype.getAcSubtype());
  }

  @Override
  public void testRemove() {
    Integer id = acSubtypeUnderTest.getId();
    remove(AcSubtype.class, id);
    assertNull(find(AcSubtype.class, id));
  }
}
