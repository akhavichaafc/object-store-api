package ca.gc.aafc.objectstore.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata.DcType;


public class ObjectStoreMetadataDto {
  private UUID uuid;

  private String dcFormat;
  private DcType dcType;

  private OffsetDateTime acDigitizationDate;
  private OffsetDateTime xmpMetadataDdate;

  private String acHashFunction;
  private String acHashValue;
 
  public UUID getUuid() {
    return uuid;
  }
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
  public String getDcFormat() {
    return dcFormat;
  }
  public void setDcFormat(String dcFormat) {
    this.dcFormat = dcFormat;
  }
  public OffsetDateTime getAcDigitizationDate() {
    return acDigitizationDate;
  }
  public void setAcDigitizationDate(OffsetDateTime acDigitizationDate) {
    this.acDigitizationDate = acDigitizationDate;
  }
  public OffsetDateTime getXmpMetadataDdate() {
    return xmpMetadataDdate;
  }
  public void setXmpMetadataDdate(OffsetDateTime xmpMetadataDdate) {
    this.xmpMetadataDdate = xmpMetadataDdate;
  }
  public String getAcHashFunction() {
    return acHashFunction;
  }
  public void setAcHashFunction(String acHashFunction) {
    this.acHashFunction = acHashFunction;
  }
  public String getAcHashValue() {
    return acHashValue;
  }
  public void setAcHashValue(String acHashValue) {
    this.acHashValue = acHashValue;
  }
  public DcType getDcType() {
    return dcType;
  }
  public void setDcType(DcType dcType) {
    this.dcType = dcType;
  }
    
}
