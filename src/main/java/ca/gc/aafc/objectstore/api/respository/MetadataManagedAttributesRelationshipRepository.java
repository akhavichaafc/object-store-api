package ca.gc.aafc.objectstore.api.respository;
import java.io.Serializable;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import io.crnk.core.repository.foward.ForwardingRelationshipRepository;


public class MetadataManagedAttributesRelationshipRepository extends ForwardingRelationshipRepository
<ObjectStoreMetadataDto, Serializable, ManagedAttributeDto, Serializable>{

}
