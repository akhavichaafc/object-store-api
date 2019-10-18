package ca.gc.aafc.objectstore.api.respository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ManyRelationshipRepositoryBase;
import io.crnk.core.repository.RelationshipMatcher;
import io.crnk.core.resource.list.ResourceList;
import lombok.extern.java.Log;

@Log
@Repository
@Transactional
public class MetadataManagedAttributesRelationshipRepository extends 
  ManyRelationshipRepositoryBase<ObjectStoreMetadataDto, UUID,  ManagedAttributeDto, UUID>{

  @Autowired
  private ObjectStoreResourceRepository objectStoreResourceRepository;
  
  @Autowired
  private ManagedAttributeResourceRepository managedAttributeResourceRepository; 
  
  @Override
  public RelationshipMatcher getMatcher() {
    RelationshipMatcher matcher = new RelationshipMatcher();
    matcher.rule().source(ObjectStoreMetadataDto.class).target(ManagedAttributeDto.class).add();
    return matcher;
  }
  
  @Override
  public Map<UUID, ResourceList<ManagedAttributeDto>> findManyRelations(Collection<UUID> sourceIds,
      String fieldName, QuerySpec querySpec) {
    Map<UUID, ResourceList<ManagedAttributeDto>> map = new HashMap<>();
    for (UUID sourceId : sourceIds) {
        log.info("findManyRelations ->" + sourceId);
        ResourceList<ManagedAttributeDto> list = managedAttributeResourceRepository.findAll(querySpec);
        map.put(sourceId, list);
    }
    return map;
  }
  
  @Override
  public void setRelations(ObjectStoreMetadataDto source, Collection<UUID> targetIds, String fieldName) {
    log.info("setRelations ->" + targetIds);
    objectStoreResourceRepository.setRelationships(source.getUuid(), new ArrayList<>(targetIds));
  }

  @Override
  public void addRelations(ObjectStoreMetadataDto source, Collection<UUID> targetIds, String fieldName) {
    log.info("addRelations ->" + targetIds);
    objectStoreResourceRepository.addRelationships(source.getUuid(), new ArrayList<>(targetIds));
  }

  @Override
  public void removeRelations(ObjectStoreMetadataDto source, Collection<UUID> targetIds, String fieldName) {
    log.info("removeRelations ->" + targetIds);
    objectStoreResourceRepository.removeRelationships(source.getUuid(), new ArrayList<>(targetIds));
  }
 
}
