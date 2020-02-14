package ca.gc.aafc.objectstore.api.service;

import java.util.Optional;
import java.util.UUID;

import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;

public interface ObjectStoreMetadataReadService {
  
  Optional<ObjectStoreMetadata> loadObjectStoreMetadata(UUID id);
  Optional<ObjectStoreMetadata> loadObjectStoreMetadataByFileId(UUID fileId);

}
