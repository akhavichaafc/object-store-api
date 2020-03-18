package ca.gc.aafc.objectstore.api.auditing;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.javers.core.Javers;
import org.springframework.context.ApplicationContext;

import ca.gc.aafc.dina.mapper.JpaDtoMapper;
import ca.gc.aafc.objectstore.api.dto.ManagedAttributeMapDto;
import ca.gc.aafc.objectstore.api.dto.ObjectStoreMetadataDto;
import ca.gc.aafc.objectstore.api.entities.ObjectStoreMetadata;
import ca.gc.aafc.objectstore.api.respository.managedattributemap.MetadataToManagedAttributeMapRepository;
import io.crnk.core.engine.internal.utils.PropertyUtils;
import io.crnk.core.engine.registry.ResourceRegistry;
import io.crnk.core.queryspec.QuerySpec;
import lombok.RequiredArgsConstructor;

@Named
@RequiredArgsConstructor
public class AuditListener implements PostUpdateEventListener, PostInsertEventListener, PostDeleteEventListener {

  private static final long serialVersionUID = -1827677058024785128L;
  private final Javers javers;
  private final EntityManagerFactory emf;

  /** Hook this listener into Hibernate's entity lifecycle methods. */
  @PostConstruct
  public void init() {
    EventListenerRegistry registry = emf.unwrap(SessionFactoryImpl.class)
        .getServiceRegistry().getService(EventListenerRegistry.class);

    registry.appendListeners(EventType.POST_INSERT, this);
    registry.appendListeners(EventType.POST_UPDATE, this);
    registry.appendListeners(EventType.POST_DELETE, this);
  }

  @Override
  public void onPostDelete(PostDeleteEvent event) {
    persistSnapshot(event);
  }

  @Override
  public void onPostInsert(PostInsertEvent event) {
    persistSnapshot(event);
  }

  @Override
  public void onPostUpdate(PostUpdateEvent event) {
    persistSnapshot(event);
  }

  private void persistSnapshot(AbstractEvent event) {
    // Replace with the actual user name after we setup authentication:
    String author = "anonymous";
    Object entity = PropertyUtils.getProperty(event, "entity");
    Object snapshot = loadSnapshot(entity);
    Class<?> eventType = event.getClass();

    if (snapshot != null) {
      if (Arrays.asList(PostInsertEvent.class, PostUpdateEvent.class).contains(eventType)) {
        javers.commit(author, snapshot);
      } else if (PostDeleteEvent.class == eventType) {
        javers.commitShallowDelete(author, snapshot);
      }
    }
  }

  private final JpaDtoMapper jpaDtoMapper;
  private final ApplicationContext ctx;
  private final MetadataToManagedAttributeMapRepository managedAttributeMapRepo;

  private Object loadSnapshot(Object entity) {
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
    }

    return null;
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    return false;
  }

}
