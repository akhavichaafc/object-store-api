package ca.gc.aafc.objectstore.api.dto;

import java.util.UUID;

import ca.gc.aafc.objectstore.api.entities.AcSubtype.DcType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;


@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@Data
@JsonApiResource(type = "ac_subtype")
public class AcSubtypeDto {
  
  @JsonApiId 
  private UUID uuid;

  private DcType dcType;
  private String subtype;
  
}
