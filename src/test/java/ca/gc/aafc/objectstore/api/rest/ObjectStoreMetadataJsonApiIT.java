package ca.gc.aafc.objectstore.api.rest;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;

import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.Agent;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.mapper.ObjectStoreMetadataMapper;
import ca.gc.aafc.objectstore.api.testsupport.factories.AgentFactory;
import ca.gc.aafc.objectstore.api.testsupport.factories.ObjectStoreMetadataFactory;

public class ObjectStoreMetadataJsonApiIT extends BaseJsonApiIntegrationTest {

  private final ObjectStoreMetadataMapper mapper = ObjectStoreMetadataMapper.INSTANCE;
  private static final String METADATA_CREATOR_PROPERTY_NAME = "acMetadataCreator";
  
  private ObjectStoreMetadata objectStoreMetadata;
  
  private final UUID agentId = UUID.randomUUID();

  @BeforeEach
  public void setup() {
    Agent agent = AgentFactory.newAgent()
        .uuid(agentId)
        .build();

    // we need to run the setup in another transaction and commit it otherwise it can't be visible
    // to the test web server.
    runInNewTransaction(em -> {
      em.persist(agent);
    });
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
    
    ObjectStoreMetadataDto objectStoreMetadatadto = mapper.toDto(objectStoreMetadata, null);
    return toAttributeMap(objectStoreMetadatadto);
  }

  @Override
  protected Map<String, Object> buildUpdateAttributeMap() {

    OffsetDateTime dateTime4TestUpdate = OffsetDateTime.now();
    objectStoreMetadata.setAcHashFunction("SHA1");
    objectStoreMetadata.setAcDigitizationDate(dateTime4TestUpdate);
    objectStoreMetadata.setXmpMetadataDate(dateTime4TestUpdate);
    objectStoreMetadata.setDcFormat("updatedTestFormat");
    ObjectStoreMetadataDto objectStoreMetadatadto = mapper.toDto(objectStoreMetadata, null);
    return toAttributeMap(objectStoreMetadatadto);
  }
  
  @Override
  protected List<Relationship> buildRelationshipList() {
    return Arrays.asList(Relationship.of(METADATA_CREATOR_PROPERTY_NAME, "agent", agentId.toString()));
  }
  
}
