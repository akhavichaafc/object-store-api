package ca.gc.aafc.objectstore.api.testsupport.factories;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.entities.DcType;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class ObjectStoreMetadataFactory implements TestableEntityFactory<ObjectStoreMetadata> {

  @Override
  public ObjectStoreMetadata getEntityInstance() {
    return newObjectStoreMetadata().build();
  }
  
  /**
   * Static method that can be called to return a configured builder that can be further customized
   * to return the actual entity object, call the .build() method on a builder.
   * 
   * @return Pre-configured builder with all mandatory fields set
   */
  public static ObjectStoreMetadata.ObjectStoreMetadataBuilder newObjectStoreMetadata() {
    
    return  ObjectStoreMetadata.builder()
        .uuid(UUID.randomUUID())
        .fileIdentifier(UUID.randomUUID())
        .fileExtension(".jpg")
        .bucket("mybucket")
        .acHashValue("abc")
        .dcType(DcType.IMAGE)
        .xmpRightsWebStatement("https://open.canada.ca/en/open-government-licence-canada")
        .dcRights("Copyright Government of Canada")
        .xmpRightsOwner("Government of Canada");
   }  
    
  /**
   * A utility method to create a list of qty number of Chains with no configuration.
   * 
   * @param qty The number of Chains populated in the list
   * @return List of Chain
   */
  public static List<ObjectStoreMetadata> newListOf(int qty) {
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
  public static List<ObjectStoreMetadata> newListOf(int qty,
      BiFunction<ObjectStoreMetadata.ObjectStoreMetadataBuilder, Integer, ObjectStoreMetadata.ObjectStoreMetadataBuilder> configuration) {
    
    return TestableEntityFactory.newEntity(qty, ObjectStoreMetadataFactory::newObjectStoreMetadata, configuration,
        ObjectStoreMetadata.ObjectStoreMetadataBuilder::build);
  }
  
}
