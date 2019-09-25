package ca.gc.aafc.objectstore.api.entities;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;

/**
* The Class ObjectStoreMeta.
*/

@Entity
@Table(name = "metadata")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="SAGESDataCache")
@SuppressFBWarnings({"EI_EXPOSE_REP","EI_EXPOSE_REP2"})
public class ObjectStoreMeta  implements java.io.Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5655824300348079540L;

  private Integer id;
  
  private UUID uuid;
  

  private String dcFormat;
  private DcType dcType;
    
  private Timestamp acDigitizationDate;
  private Timestamp xmpMetadataDdate;
  
  private String acHashFunction;
  private String acHashValue;
  
  public enum DcType {
    IMAGE ("Image"),
    MOVING_IMAGE ("Moving Image"),
    SOUND ("Sound"),
    TEXT ("Text");
    
    /** The value. */
    private final String value;
    
    /**
     * Instantiates a new dc_type.
     *
     * @param value the value
     */
    DcType (String value){
      this.value = value;
    }
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
      return value;
    }
  }

  @Id
  @GeneratedValue( strategy = GenerationType.IDENTITY )
  @Column( name = "id" )  
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
  
  @NotNull
  @Column(name = "uuid", unique = true)
  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  
  @Column(name = "dc_format")
  public String getDcFormat() {
    return dcFormat;
  }

  public void setDcFormat(String dcFormat) {
    this.dcFormat = dcFormat;
  }

  @NotNull
  @Type( type = "pgsql_enum" )
  @Enumerated( EnumType.STRING )
  @Column( name = "dc_type" )  
  public DcType getDcType() {
    return dcType;
  }

  public void setDcType(DcType dcType) {
    this.dcType = dcType;
  }
  
  @Column( name = "ac_digitization_date" )
  public Timestamp getAcDigitizationDate() {
    return acDigitizationDate;
  }

  public void setAcDigitizationDate(Timestamp acDigitizationDate) {
    this.acDigitizationDate = acDigitizationDate;
  }
  
  @Column(name = "xmp_metadata_date")
  public Timestamp getXmpMetadataDdate() {
    return xmpMetadataDdate;
  }

  public void setXmpMetadataDdate(Timestamp xmpMetadataDdate) {
    this.xmpMetadataDdate = xmpMetadataDdate;
  }

  @Column(name = "ac_hash_function")  
  public String getAcHashFunction() {
    return acHashFunction;
  }

  public void setAcHashFunction(String acHashFunction) {
    this.acHashFunction = acHashFunction;
  }

  @Column(name = "ac_hash_value")  
  public String getAcHashValue() {
    return acHashValue;
  }

  public void setAcHashValue(String acHashValue) {
    this.acHashValue = acHashValue;
  }
  
  @Builder
  public ObjectStoreMeta(UUID uuid, String dcFormat, DcType dcType, 
      Timestamp acDigitizationDate, Timestamp xmpMetadataDate,
      String acHashFunction, String acHashValue) {
    this.uuid = uuid;
    this.dcFormat = dcFormat;
    this.dcType = dcType;
    this.acHashFunction = acHashFunction;
    this.acHashValue = acHashValue;   
    
  }
  
  public ObjectStoreMeta() {
    
  }
  
}

  
