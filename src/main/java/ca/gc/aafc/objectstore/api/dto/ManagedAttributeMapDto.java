package ca.gc.aafc.objectstore.api.dto;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Builder;
import lombok.Data;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@Data
@JsonApiResource(type = "managed-attribute-map")
@Builder
public class ManagedAttributeMapDto {

  @JsonApiId
  private String id;

  /**
   * Map of ManagedAttribute UUIDs to values.
   */
  @Builder.Default
  private Map<String, ManagedAttributeMapValue> values = new HashMap<>();

  @Data
  @Builder
  public static class ManagedAttributeMapValue {
    private String name;
    private String value;
  }

}