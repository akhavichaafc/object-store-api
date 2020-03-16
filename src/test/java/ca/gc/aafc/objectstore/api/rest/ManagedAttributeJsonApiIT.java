package ca.gc.aafc.objectstore.api.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;

public class ManagedAttributeJsonApiIT extends BaseJsonApiIntegrationTest {

  private ManagedAttributeDto managedAttribute;
  
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
    List<String> acceptedValues  = Arrays.asList("CataloguedObject");
    
    managedAttribute = new ManagedAttributeDto();
    managedAttribute.setAcceptedValues(acceptedValues);
    managedAttribute.setUuid(null);
    
    return toAttributeMap(managedAttribute);
  }

  @Override
  protected Map<String, Object> buildUpdateAttributeMap() {
    List<String> acceptedValues  = Arrays.asList("dorsal");
    
    managedAttribute.setName("specimen_view");
    managedAttribute.setAcceptedValues(acceptedValues);
    
    return toAttributeMap(managedAttribute);
  }
}
