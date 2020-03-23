package ca.gc.aafc.objectstore.api.auditing;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.javers.core.Javers;

import ca.gc.aafc.dina.entity.SoftDeletable;
import io.crnk.core.engine.internal.utils.PropertyUtils;
import lombok.RequiredArgsConstructor;

/**
 * Hooks into Hibernate to listen for create/update/delete events so it can store audits of changed data.
 */
@Named
@RequiredArgsConstructor
public class AuditListener implements PostUpdateEventListener, PostInsertEventListener, PreDeleteEventListener {

  private static final long serialVersionUID = -1827677058024785128L;
  private final Javers javers;
  private final EntityManagerFactory emf;
  private final SnapshotLoader snapshotLoader;

  /** Hook this listener into Hibernate's entity lifecycle methods. */
  @PostConstruct
  public void init() {
    EventListenerRegistry registry = emf.unwrap(SessionFactoryImpl.class)
        .getServiceRegistry().getService(EventListenerRegistry.class);

    registry.appendListeners(EventType.POST_INSERT, this);
    registry.appendListeners(EventType.POST_UPDATE, this);
    registry.appendListeners(EventType.PRE_DELETE, this);
  }

  @Override
  public boolean onPreDelete(PreDeleteEvent event) {
    persistSnapshot(event);
    return false;
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
    Object snapshot = snapshotLoader.loadSnapshot(entity);

    boolean softDeleted = (entity instanceof SoftDeletable) && ((SoftDeletable) entity).getDeletedDate() != null;  
    
    if (snapshot != null) {
      Class<?> eventType = event.getClass();

      // Soft Deletes and real deletes are both treated as audit deletes:
      if (softDeleted || eventType == PostDeleteEvent.class) {
        javers.commitShallowDelete(author, snapshot);
      } else if (eventType == PostInsertEvent.class || eventType == PostUpdateEvent.class) {
        javers.commit(author, snapshot);
      }
    }
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    return false;
  }

}
