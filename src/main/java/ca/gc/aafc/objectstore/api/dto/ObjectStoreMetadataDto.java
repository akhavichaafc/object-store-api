package ca.gc.aafc.objectstore.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata.DcType;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@JsonApiResource(type = "metadata")
public class ObjectStoreMetadataDto {
  
  @JsonApiId
  private UUID uuid;

  private String dcFormat;
  private DcType dcType;

  private OffsetDateTime acDigitizationDate;
  private OffsetDateTime xmpMetadataDate;

  private String acHashFunction;
  private String acHashValue;
  
  @JsonApiRelation
  private List<ManagedAttributeDto> managedAttributes;
  
/*  @JsonIgnore
  private RelationshipData relationshipData;
  
  public void addRelationshipData(Class<?> dtoClass, UUID uuid) {
    if( relationshipData == null) {
      relationshipData = new RelationshipData();
    }
    relationshipData.addRelationshipData(dtoClass, uuid);
  }*/
}
