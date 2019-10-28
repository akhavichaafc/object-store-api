package ca.gc.aafc.objectstore.api.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;

import ca.gc.aafc.objectstore.api.dto.MetadataManagedAttributeDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.MetadataManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.mapper.MetadataManagedAttributeMapper;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.MetadataManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class MetadataManagedAttributeJsonApiIT extends BaseJsonApiIntegrationTest {

  private MetadataManagedAttributeMapper mapper = MetadataManagedAttributeMapper.INSTANCE;
  
  private MetadataManagedAttribute mmaCreated;

  private UUID managedAttributeId = UUID.randomUUID();
  private UUID metadataId = UUID.randomUUID();

  @BeforeEach
  public void setup() {
    ManagedAttribute ma = ManagedAttributeFactory.newManagedAttribute().uuid(managedAttributeId)
        .build();
    ObjectStoreMetadata osm = ObjectStoreMetadataFactory.newObjectStoreMetadata().uuid(metadataId).build();

    // we need to run the setup in another transaction and commit it otherwise it can't be visible
    // to the test web server.
    runInNewTransaction(em -> {
      em.persist(ma);
      em.persist(osm);
    });
  }

  @Override
  protected String getResourceUnderTest() {
    return "metadata-managed-attribute";
  }

  @Override
  protected String getGetOneSchemaFilename() {
    return null;
  }

  @Override
  protected String getGetManySchemaFilename() {
    return null;
  }

  @Override
  protected Map<String, Object> buildCreateAttributeMap() {

    mmaCreated = MetadataManagedAttributeFactory.newMetadataManagedAttribute()
        .uuid(null)
        .managedAttribute(null)
        .objectStoreMetadata(null)
        .build();

    MetadataManagedAttributeDto objectStoreMetadatadto = mapper.toDto(mmaCreated);
    return toAttributeMap(objectStoreMetadatadto);
  }

  @Override
  protected Map<String, Object> buildUpdateAttributeMap() {
    mmaCreated.setAssignedValue("zxy");
    MetadataManagedAttributeDto objectStoreMetadatadto = mapper.toDto(mmaCreated);
    return toAttributeMap(objectStoreMetadatadto);
  }

  @Override
  protected List<Relationship> buildRelationshipList() {
    return Arrays.asList(
        Relationship.of("managedAttribute", "managed-attribute", managedAttributeId.toString()),
        Relationship.of("objectStoreMetadata", "metadata", metadataId.toString()));
  }

}
