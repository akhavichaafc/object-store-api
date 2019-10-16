package ca.gc.aafc.objectstore.api.respository;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.repository.RelationshipRepositoryV2;
import io.katharsis.resource.list.ResourceList;

public class MetadataManagedAttributesRelationshipRepository implements
    RelationshipRepositoryV2<ObjectStoreMetadataDto, Serializable, ManagedAttributeDto, Serializable> {

  @Autowired
  private ObjectStoreResourceRepository objectStoreResourceRepository;

  @Autowired
  private ManagedAttributeResourceRepository managedAttributeResourceRepository;
  

  @Override
  public void addRelations(ObjectStoreMetadataDto objectStoreMetadataDto, Iterable<Serializable> managedAttributeIds, String arg2) {
    List<ManagedAttributeDto> managedAttributes = objectStoreMetadataDto.getManagedAttributes();
    managedAttributes.addAll(managedAttributeResourceRepository.findAll(managedAttributeIds));
    objectStoreMetadataDto.setManagedAttributes(managedAttributes);
    objectStoreResourceRepository.save(objectStoreMetadataDto);
    
  }

  @Override
  public ResourceList<ManagedAttributeDto> findManyTargets(Serializable arg0, String arg1,
      QuerySpec arg2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ManagedAttributeDto findOneTarget(Serializable arg0, String arg1, QuerySpec arg2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeRelations(ObjectStoreMetadataDto arg0, Iterable<Serializable> arg1,
      String arg2) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setRelation(ObjectStoreMetadataDto arg0, Serializable arg1, String arg2) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setRelations(ObjectStoreMetadataDto objectStoreMetadataDto, Iterable<Serializable> managedAttributeIds, String arg2) {
    List<ManagedAttributeDto> managedAttributes = managedAttributeResourceRepository.findAll(managedAttributeIds);
    objectStoreMetadataDto.setManagedAttributes(managedAttributes);
    objectStoreResourceRepository.save(objectStoreMetadataDto);
    
  }

  @Override
  public Class<ObjectStoreMetadataDto> getSourceResourceClass() {
    return ObjectStoreMetadataDto.class;
  }

  @Override
  public Class<ManagedAttributeDto> getTargetResourceClass() {
    return ManagedAttributeDto.class;
  }
}
