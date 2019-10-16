package ca.gc.aafc.objectstore.api.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.gc.aafc.objectstore.api.interfaces.UniqueObj;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class MetadataManagedAttribute implements Serializable, UniqueObj {

  private static final long serialVersionUID = -3484692979076302405L;
  private Integer id;
  private ObjectStoreMetadata objectStoreMetadata;
  private ManagedAttribute managedAttribute;
  private String[] assignedValues;  
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  
  @Column(name = "id")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
  
  @NotNull
  @ManyToOne(cascade = {} )
  @JoinColumn(name = "metadata_id")
  public ObjectStoreMetadata getObjectStoreMetadata() {
    return objectStoreMetadata;
  }

  public void setObjectStoreMetadata(ObjectStoreMetadata objectStoreMetadata) {
    this.objectStoreMetadata = objectStoreMetadata;
  }

  @NotNull
  @ManyToOne(cascade = {})
  @JoinColumn(name = "managed_attribute_id")
  public ManagedAttribute getManagedAttribute() {
    return managedAttribute;
  }

  public void setManagedAttribute(ManagedAttribute managedAttribute) {
    this.managedAttribute = managedAttribute;
  }
  
  public String[] getAssignedValues() {
    return assignedValues;
  }

  public void setAssignedValues(String[] assignedValues) {
    this.assignedValues = assignedValues;
  }

}


