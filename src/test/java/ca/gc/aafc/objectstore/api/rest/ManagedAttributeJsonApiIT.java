package ca.gc.aafc.objectstore.api.rest;

import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.mapper.ManagedAttributeMapper;
import ca.gc.aafc.objectstore.api.testsupport.factories.ManagedAttributeFactory;
import io.restassured.response.ValidatableResponse;

public class ManagedAttributeJsonApiIT extends BaseJsonApiIntegrationTest {

  private ManagedAttributeMapper mapper = ManagedAttributeMapper.INSTANCE;
  
  private ManagedAttribute managedAttribute;
  
  @Override
  protected String getResourceUnderTest() {
    return "managed-attribute";
  }

  @Override
  protected String getGetOneSchemaFilename() {
    return "getOneManagedAttributeSchema.json";
  }

  @Override
  protected String getGetManySchemaFilename() {
    return null;
  }

  @Override
  protected Map<String, Object> buildCreateAttributeMap() {
    String[] acceptedValues  = new String[] {"CataloguedObject"};
    
    managedAttribute = ManagedAttributeFactory.newManagedAttribute()
      .acceptedValues(acceptedValues)
      .uuid(null)
      .build();
    ManagedAttributeDto managedAttributeDto = mapper.toDto(managedAttribute);
    return toAttributeMap(managedAttributeDto);

  }

  @Override
  protected Map<String, Object> buildUpdateAttributeMap() {
    
    String[] acceptedValues  = new String[] {"dorsal"};
    
    managedAttribute.setName("specimen_view");
    managedAttribute.setAcceptedValues(acceptedValues);
    
    ManagedAttributeDto managedAttributeDto = mapper.toDto(managedAttribute);
    return toAttributeMap(managedAttributeDto);
    
  }

  @Test
  public void resourceUnderTest_whenDeleteExisting_softDeletes() {
    String id = sendPost(toJsonAPIMap(buildCreateAttributeMap(), toRelationshipMap(buildRelationshipList())));

    sendDelete(id);

    // get list should not return deleted resource
    ValidatableResponse responseUpdate = sendGet("");
    responseUpdate.body("data.id", Matchers.not(Matchers.hasItem(Matchers.containsString(id))));

    // get list should return deleted resource with deleted filter
    responseUpdate = sendGet("?filter[deletedDate][NEQ]=null");
    responseUpdate.body("data.id", Matchers.hasItem(Matchers.containsString(id)));

    // get one throws gone 410 as expected
    sendGet(id, 410);

    // get one resource is available with the deleted filter
    sendGet(id + "?filter[deletedDate][NEQ]=null");
  }
}
