package ca.gc.aafc.objectstore.api.respository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dto.ManagedAttributeMapDto;
import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;

/**
 * Crnk requires a repository for each resource type.
 * However, ManagedAttributeMap is a derived/generated object that  
 */
@Repository
public class ManagedAttributeMapRepository extends ResourceRepositoryBase<ManagedAttributeMapDto, UUID> {

  public ManagedAttributeMapRepository() {
    super(ManagedAttributeMapDto.class);
  }

  @Override
  public ResourceList<ManagedAttributeMapDto> findAll(QuerySpec querySpec) {
    throw new MethodNotAllowedException("method not allowed");
  }

}