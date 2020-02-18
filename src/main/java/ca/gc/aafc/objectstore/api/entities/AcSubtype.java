package ca.gc.aafc.objectstore.api.entities;

import java.util.Optional;
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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata.DcType;
import ca.gc.aafc.objectstore.api.interfaces.UniqueObj;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "ac_subtype")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@Builder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@NaturalIdCache
public class AcSubtype implements java.io.Serializable, UniqueObj {

  private static final long serialVersionUID = 1L;
  private Integer id;
  private DcType dcType;
  private String subtype;
  private UUID uuid;  
  
  @NaturalId
  @NotNull
  @Column(name = "uuid", unique = true)
  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }  
  @Column(name = "subtype")  
  public String getSubtype() {
    return subtype;
  }

  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  
  @NotNull
  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  @Column(name = "dc_type")
  public DcType getDcType() {
    return dcType;
  }

  public void setDcType(DcType dcType) {
    this.dcType = dcType;
  }

  public enum DcType {
    IMAGE("Image"),
    MOVING_IMAGE("Moving Image", "video"),
    SOUND("Sound"),
    TEXT("Text"),
    DATASET("Dataset"), // Data encoded in a defined structure
    UNDETERMINED("Undetermined");

    private final String value;
    private final String dcFormatType;

    DcType(String value) {
      this(value, value.toLowerCase());
    }
    
    /**
     * Main DcType constructor.
     * 
     * @param value
     * @param dcFormatType
     *          represent the first part of the media type. For text/csv the dcFormatType would be
     *          "text".
     */
    DcType(String value, String dcFormatType) {
      this.value = value;
      this.dcFormatType = dcFormatType;
    }

    public String getValue() {
      return value;
    }
    
    public String getDcFormatType() {
      return dcFormatType;
    }

    /**
     * Get the {@link DcType} value from the provided string. The string is matched in a case
     * insensitive manner.
     * 
     * @param value
     * @return the {@link DcType} wrapped in an {@link Optional} or {@link Optional#empty()} is no
     *         there is {@link DcType} match.
     */
    public static Optional<DcType> fromValue(String value) {
      for (DcType currType : values()) {
        if (currType.getValue().equalsIgnoreCase(value)) {
          return Optional.of(currType);
        }
      }
      return Optional.empty();
    }
    
    /**
     * Get the {@link DcType} value associated with the provided dcFormat. The string is matched in
     * a case insensitive manner. dcFormat is expected to be in the form of media type (e.g.
     * text/csv).
     * 
     * @param value
     *          in the form of media type (e.g. text/csv)
     * @return the {@link DcType} wrapped in an {@link Optional} or {@link Optional#empty()} if
     *         there is no match.
     */
    public static Optional<DcType> fromDcFormat(String dcFormat) {
      if (dcFormat == null) {
        return Optional.empty();
      }
      String dcFormatType = StringUtils.substringBefore(dcFormat, "/");

      for (DcType currType : values()) {
        if (currType.getDcFormatType().equalsIgnoreCase(dcFormatType)) {
          return Optional.of(currType);
        }
      }
      return Optional.empty();
    }
  }

}
