package ca.gc.aafc.objectstore.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.objectstore.api.entities.DcType;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.LookupIncludeBehavior;
import lombok.Data;

@Data
@JsonApiResource(type = ObjectStoreMetadataDto.TYPENAME)
@TypeName(ObjectStoreMetadataDto.TYPENAME)
public class ObjectStoreMetadataDto {
  
  public static final String TYPENAME = "metadata";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;
  
  private String bucket;
  private UUID fileIdentifier;
  private String fileExtension;

  private String dcFormat;
  private DcType dcType;
  
  @JsonInclude(Include.NON_EMPTY)
  private String acCaption;

  private OffsetDateTime acDigitizationDate;

  @DiffIgnore
  private OffsetDateTime xmpMetadataDate;
  
  private String xmpRightsWebStatement;
  private String dcRights;
  private String xmpRightsOwner;
  
  @JsonInclude(Include.NON_EMPTY)
  private String originalFilename;

  private String acHashFunction;
  private String acHashValue;

  @DiffIgnore
  private OffsetDateTime createdDate;
  @JsonInclude(Include.NON_EMPTY)
  private OffsetDateTime deletedDate;
  
  @JsonInclude(Include.NON_EMPTY)
  private Set<String> acTags;
  
  @JsonApiRelation
  private List<MetadataManagedAttributeDto> managedAttribute;

  // AUTOMATICALLY_ALWAYS because it should be fetched using a call to
  // MetadataToManagedAttributeMapRepository.
  @JsonApiRelation(lookUp = LookupIncludeBehavior.AUTOMATICALLY_ALWAYS)
  private ManagedAttributeMapDto managedAttributeMap;
  
  @JsonApiRelation
  @DiffIgnore // Agent fields will be replaced with references to external data, so don't audit them yet.
  private AgentDto acMetadataCreator;
  
  @JsonApiRelation
  private ObjectStoreMetadataDto acDerivedFrom;
  
  @JsonApiRelation
  @DiffIgnore // Agent fields will be replaced with references to external data, so don't audit them yet.
  private AgentDto dcCreator;

  private boolean publiclyReleasable;

  @JsonInclude(Include.NON_EMPTY)
  private String notPubliclyReleasableReason;

}
