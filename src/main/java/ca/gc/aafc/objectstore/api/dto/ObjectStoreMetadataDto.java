package ca.gc.aafc.objectstore.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata.DcType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectStoreMetadataDto {
  private UUID uuid;

  private String dcFormat;
  private DcType dcType;

  private OffsetDateTime acDigitizationDate;
  private OffsetDateTime xmpMetadataDdate;

  private String acHashFunction;
  private String acHashValue;

}
