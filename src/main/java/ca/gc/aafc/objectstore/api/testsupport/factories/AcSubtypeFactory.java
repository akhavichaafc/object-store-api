package ca.gc.aafc.objectstore.api.testsupport.factories;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.objectstore.api.entities.AcSubtype;
import ca.gc.aafc.objectstore.api.entities.DcType;

public class AcSubtypeFactory implements TestableEntityFactory<AcSubtype> {

  @Override
  public AcSubtype getEntityInstance() {
    return newAcSubtype().build();
  }
  
  /**
   * Static method that can be called to return a configured builder that can be further customized
   * to return the actual entity object, call the .build() method on a builder.
   * 
   * @return Pre-configured builder with all mandatory fields set
   */
  public static AcSubtype.AcSubtypeBuilder newAcSubtype() {
    return AcSubtype.builder()
        .uuid(UUID.randomUUID())
        .dcType(DcType.IMAGE)
        .subtype("supporting specilzation");
   } 
  
  /**
   * A utility method to create a list of qty number of AcSubtype with no configuration.
   * 
   * @param qty The number of AcSubtype populated in the list
   * @return List of AcSubtype
   */
  public static List<AcSubtype> newListOf(int qty) {
    return newListOf(qty, null);
  }

  /**
   * A utility method to create a list of qty number of AcSubtype with an incrementing attribute
   * based on the configuration argument. An example of configuration would be the functional
   * interface (bldr, index) -> bldr.name(" string" + index)
   * 
   * @param qty           The number of AcSubtype that is populated in the list.
   * @param configuration the function to apply, usually to differentiate the different entities in
   *                      the list.
   * @return List of AcSubtype
   */
  public static List<AcSubtype> newListOf(int qty,
      BiFunction<AcSubtype.AcSubtypeBuilder, Integer, AcSubtype.AcSubtypeBuilder> configuration) {
    
    return TestableEntityFactory.newEntity(qty, AcSubtypeFactory::newAcSubtype, configuration,
        AcSubtype.AcSubtypeBuilder::build);
  }

}
