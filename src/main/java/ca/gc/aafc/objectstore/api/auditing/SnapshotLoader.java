package ca.gc.aafc.objectstore.api.auditing;

import java.util.Arrays;

import javax.inject.Named;

import org.springframework.context.ApplicationContext;

import ca.gc.aafc.dina.mapper.JpaDtoMapper;
import ca.gc.aafc.objectstore.api.dto.ManagedAttributeMapDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.respository.managedattributemap.MetadataToManagedAttributeMapRepository;
import io.crnk.core.engine.registry.ResourceRegistry;
import io.crnk.core.queryspec.QuerySpec;
import lombok.RequiredArgsConstructor;

/**
 * Provides entity-specific loading of audit snapshots.
 */
@Named
@RequiredArgsConstructor
public class SnapshotLoader {

  private final JpaDtoMapper jpaDtoMapper;
  private final ApplicationContext ctx;
  private final MetadataToManagedAttributeMapRepository managedAttributeMapRepo;

  public Object loadSnapshot(Object entity) {
    Class<?> clazz = entity.getClass();
    ResourceRegistry resourceRegistry = ctx.getBean(ResourceRegistry.class);

    if (Arrays.asList(ObjectStoreMetadata.class).contains(clazz)) {
      QuerySpec querySpec = new QuerySpec(ObjectStoreMetadataDto.class);
      
      ObjectStoreMetadataDto metadata = (ObjectStoreMetadataDto) jpaDtoMapper
          .toDto(entity, querySpec, resourceRegistry);

      // Fetch the managed attribute map from the repo, because it isn't included through the QuerySpec.
      ManagedAttributeMapDto attributeMap = managedAttributeMapRepo
          .getAttributeMapFromMetadataId(metadata.getUuid());
      metadata.setManagedAttributeMap(attributeMap);

      return metadata;
    } else {
      // More 'else' blocks should be added here to audit other entity types.
    }

    return null;
  }

}