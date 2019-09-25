package ca.gc.aafc.objectstore.api.testsupport.factories;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMeta;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMeta.DcType;

public class ObjectStoreMetaFactory implements TestableEntityFactory<ObjectStoreMeta> {

  @Override
  public ObjectStoreMeta getEntityInstance() {
    return newObjectStoreMeta().build();
  }
  
  /**
   * Static method that can be called to return a configured builder that can be further customized
   * to return the actual entity object, call the .build() method on a builder.
   * 
   * @return Pre-configured builder with all mandatory fields set
   */
  public static ObjectStoreMeta.ObjectStoreMetaBuilder newObjectStoreMeta() {
    
    return  ObjectStoreMeta.builder()
        .uuid(UUID.randomUUID())
        .dcFormat("dc_format")
        .dcType(DcType.IMAGE)
        .acHashFunction("MD5")
        .acHashValue("9, 4, 248, 102, 77, 97, 142, 201");
   }  
    
  /**
   * A utility method to create a list of qty number of Chains with no configuration.
   * 
   * @param qty The number of Chains populated in the list
   * @return List of Chain
   */
  public static List<ObjectStoreMeta> newListOf(int qty) {
    return newListOf(qty, null);
  }

  /**
   * A utility method to create a list of qty number of Chain with an incrementing attribute
   * based on the configuration argument. An example of configuration would be the functional
   * interface (bldr, index) -> bldr.name(" string" + index)
   * 
   * @param qty           The number of Chain that is populated in the list.
   * @param configuration the function to apply, usually to differentiate the different entities in
   *                      the list.
   * @return List of Chain
   */
  public static List<ObjectStoreMeta> newListOf(int qty,
      BiFunction<ObjectStoreMeta.ObjectStoreMetaBuilder, Integer, ObjectStoreMeta.ObjectStoreMetaBuilder> configuration) {
    
    return TestableEntityFactory.newEntity(qty, ObjectStoreMetaFactory::newObjectStoreMeta, configuration,
        ObjectStoreMeta.ObjectStoreMetaBuilder::build);
  }
  
}
