package ca.gc.aafc.objectstore.api.dto;

import java.util.HashMap;
import java.util.Map;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@Data
@JsonApiResource(type = ManagedAttributeMapDto.TYPENAME)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeName(ManagedAttributeMapDto.TYPENAME)
public class ManagedAttributeMapDto {

  public static final String TYPENAME = "managed-attribute-map";

  @Id
  @JsonApiId
  private String id;

  /**
   * Map of ManagedAttribute UUIDs to values.
   */
  @Builder.Default
  private Map<String, ManagedAttributeMapValue> values = new HashMap<>();

  @JsonApiRelation
  private ObjectStoreMetadataDto metadata;

  @Data
  @Builder
  @TypeName(ManagedAttributeMapValue.TYPENAME)
  public static class ManagedAttributeMapValue {

    public static final String TYPENAME = "managed-attribute-map-value";

    // Don't include the attribute name in audits, because it doesn't "belong" to the ManagedAttributeMap.
    // It can change independently of the Metadata or ManagedAttributeMap.
    @DiffIgnore
    private String name;

    private String value;

  }

}
