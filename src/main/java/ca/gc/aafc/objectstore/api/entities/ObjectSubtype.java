package ca.gc.aafc.objectstore.api.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import ca.gc.aafc.objectstore.api.interfaces.UniqueObj;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "object_subtype")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@Builder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@NaturalIdCache
public class ObjectSubtype implements java.io.Serializable, UniqueObj {

  private static final long serialVersionUID = 1L;
  private Integer id;
  private DcType dcType;
  private String acSubtype;
  
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
  
  @Column(name = "ac_subtype")  
  @Size(max=50)
  @NotBlank
  public String getAcSubtype() {
    return acSubtype;
  }

  public void setAcSubtype(String acSubtype) {
    this.acSubtype = acSubtype;
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

  
}
