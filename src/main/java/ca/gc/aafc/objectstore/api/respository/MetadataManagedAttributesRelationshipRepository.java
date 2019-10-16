package ca.gc.aafc.objectstore.api.respository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import io.crnk.core.repository.OneRelationshipRepositoryBase;
import io.crnk.core.repository.RelationshipMatcher;
import io.crnk.core.resource.list.ResourceList;

@Repository
@Transactional
public class MetadataManagedAttributesRelationshipRepository extends OneRelationshipRepositoryBase<ManagedAttributeDto, UUID, ObjectStoreMetadataDto, UUID>{

  private ObjectStoreResourceRepository objectStoreResourceRepository;
  
  @Autowired
  public MetadataManagedAttributesRelationshipRepository(ObjectStoreResourceRepository objectStoreResourceRepository) {
    this.objectStoreResourceRepository = objectStoreResourceRepository;
  }

  @Override
  public RelationshipMatcher getMatcher() {
    RelationshipMatcher matcher = new RelationshipMatcher();
    matcher.rule().source(ManagedAttributeDto.class).target(ObjectStoreMetadataDto.class).add();
    return matcher;
  }

  @Override
  public void setRelation(ManagedAttributeDto source, UUID targetId, String fieldName) {
    ObjectStoreMetadataDto dto = objectStoreResourceRepository.findOne(targetId, null);
    dto.getManagedAttributes().add(source);
  }

  @Override
  public Map<UUID, ObjectStoreMetadataDto> findOneRelations(Collection<UUID> sourceIds,
      String fieldName, io.crnk.core.queryspec.QuerySpec querySpec) {
    Map<UUID, ObjectStoreMetadataDto> map = new HashMap<>();
    for (UUID sourceId : sourceIds) {
        // a real-world implementation would do something more reasonable here...
        ResourceList<ObjectStoreMetadataDto> list = objectStoreResourceRepository.findAll(querySpec);
        map.put(sourceId, list.isEmpty() ? null : list.get(0));
    }
    return map;
  }
  

 
}
