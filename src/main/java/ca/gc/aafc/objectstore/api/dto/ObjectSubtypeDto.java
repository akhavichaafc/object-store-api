package ca.gc.aafc.objectstore.api.dto;

import java.util.UUID;

import ca.gc.aafc.objectstore.api.entities.DcType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;


@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@Data
@JsonApiResource(type = "object-subtype")
public class ObjectSubtypeDto {
  
  @JsonApiId 
  private UUID uuid;

  private DcType dcType;
  private String acSubtype;
  
}
