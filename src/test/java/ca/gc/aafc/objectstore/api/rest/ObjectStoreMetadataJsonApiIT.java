package ca.gc.aafc.objectstore.api.rest;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;

import com.google.common.collect.ImmutableMap;

import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.mapper.ObjectStoreMetadataMapper;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class ObjectStoreMetadataJsonApiIT extends BaseJsonApiIntegrationTest {

  private ObjectStoreMetadataMapper mapper = ObjectStoreMetadataMapper.INSTANCE;
  
  private ObjectStoreMetadata objectStoreMetadata;
  
  private UUID managedAttributeId = UUID.randomUUID();
  
  @BeforeEach
  public void setup() {
    ManagedAttribute ma = ManagedAttributeFactory.newManagedAttribute()
        .uuid(managedAttributeId).build();
    // we need to run the setup in another transaction and commit it otherwise it can't be visible to the test web server.
    runInNewTransaction( em -> em.persist(ma));
  }
  
  @Override
  protected String getResourceUnderTest() {
    return "metadata";
  }

  @Override
  protected String getGetOneSchemaFilename() {
    return "getOneMetadataSchema.json";
  }

  @Override
  protected String getGetManySchemaFilename() {
    return null;
  }

  @Override
  protected Map<String, Object> buildCreateAttributeMap() {
    
    OffsetDateTime dateTime4Test = OffsetDateTime.now();
    objectStoreMetadata = ObjectStoreMetadataFactory.newObjectStoreMetadata()
       .acHashFunction("MD5")
       .acDigitizationDate(dateTime4Test)
       .xmpMetadataDate(dateTime4Test)
       .dcFormat("testFormat")
      .build();
    
    ObjectStoreMetadataDto objectStoreMetadatadto = mapper.toDto(objectStoreMetadata);
    return toAttributeMap(objectStoreMetadatadto);
  }

  @Override
  protected Map<String, Object> buildUpdateAttributeMap() {

    OffsetDateTime dateTime4TestUpdate = OffsetDateTime.now();
    objectStoreMetadata.setAcHashFunction("SHA1");
    objectStoreMetadata.setAcDigitizationDate(dateTime4TestUpdate);
    objectStoreMetadata.setXmpMetadataDate(dateTime4TestUpdate);
    objectStoreMetadata.setDcFormat("updatedTestFormat");
    ObjectStoreMetadataDto objectStoreMetadatadto = mapper.toDto(objectStoreMetadata);
    
    return toAttributeMap(objectStoreMetadatadto);
  }
  
  @Override
  protected Map<String, Object> buildRelationshipMap() {
    ImmutableMap.Builder<String, Object> relationships = new ImmutableMap.Builder<>();
    relationships.put("type", "managed-attribute").put("id", managedAttributeId.toString()).build();

    ImmutableMap.Builder<String, Object> bldr = new ImmutableMap.Builder<>();
    bldr.put("data", relationships.build());
    return ImmutableMap.of("managedAttributes", bldr.build());
  }
  
}
