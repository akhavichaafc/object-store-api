package ca.gc.aafc.objectstore.api.dao;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;

import org.hibernate.Session;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.springframework.stereotype.Component;

/**
 * Base Data Access Object layer. This class should be the only one holding a reference to the {@link EntityManager}.
 *
 */
@Component
public class BaseDAO {
  
  @PersistenceContext
  private EntityManager entityManager;
  
  /**
   * This method can be used to inject the EntityManager into an external object.
   * 
   * @param emConsumer
   */
  public <T> T createWithEntityManager(Function<EntityManager, T> creator) {
    Objects.requireNonNull(creator);
    return creator.apply(entityManager);
  }
  
  /**
   * Utility function that can check if a lazy loaded attribute is actually loaded.
   * @param entity
   * @param fieldName
   * @return
   */
  public Boolean isLoaded(Object entity, String fieldName) {
    Objects.requireNonNull(entity);
    Objects.requireNonNull(fieldName);

    PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory()
        .getPersistenceUnitUtil();
    return unitUtil.isLoaded(entity, fieldName);
  }

  /**
   * Find an entity by it's naturalId. The method assumes that the naturalId is unique.
   * 
   * @param uuid
   * @param entityClass
   * @return
   */
  public <T> T findOneByNaturalId(UUID uuid, Class<T> entityClass) {
    T objectStoreMetadata = entityManager.unwrap(Session.class)
        .bySimpleNaturalId(entityClass)
        .load(uuid);
    return objectStoreMetadata;
  }
  
  /**
   * Give a reference to an entity that should exist without actually loading it. Useful to set
   * relationships without loading the entity.
   * 
   * @param entityClass
   * @param uuid
   * @return
   */
  public <T> T getReferenceByNaturalId(Class<T> entityClass, UUID uuid) {
    SimpleNaturalIdLoadAccess<T> loadAccess = entityManager.unwrap(Session.class)
        .bySimpleNaturalId(entityClass);
    return loadAccess.getReference(uuid);
  }
  
  /**
   * Set a relationship by calling the provided {@link Consumer} with a reference Entity loaded by
   * NaturalId.
   * 
   * @param entityClass
   * @param uuid
   * @param objConsumer
   */
  public <T> void setRelationshipUsing(Class<T> entityClass, UUID uuid, Consumer<T> objConsumer) {
    objConsumer.accept(getReferenceByNaturalId(entityClass, uuid));
  }
  
  /**
   * Save the provided entity.
   * 
   * @param entity
   */
  public void save(Object entity) {
    entityManager.persist(entity);
  }
  
  /**
   * Delete the provided entity.
   * 
   * @param entity
   */
  public void delete(Object entity) {
    entityManager.remove(entity);
  }
  
}
