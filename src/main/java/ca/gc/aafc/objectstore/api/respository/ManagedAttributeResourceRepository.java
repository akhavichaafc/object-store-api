package ca.gc.aafc.objectstore.api.respository;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.objectstore.api.dao.BaseDAO;
import ca.gc.aafc.objectstore.api.dto.ManagedAttributeDto;
import ca.gc.aafc.objectstore.api.entities.ManagedAttribute;
import ca.gc.aafc.objectstore.api.mapper.ManagedAttributeMapper;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;

@Repository
@Transactional
public class ManagedAttributeResourceRepository extends ResourceRepositoryBase<ManagedAttributeDto, UUID> {

  private final BaseDAO dao;
  private final ManagedAttributeMapper mapper;

  @Inject
  public ManagedAttributeResourceRepository(BaseDAO dao, ManagedAttributeMapper mapper) {
    super(ManagedAttributeDto.class);
    this.dao = dao;
    this.mapper = mapper;
  }

  /**
   * @param resource
   *          to save
   * @return saved resource
   */
  @Override
  public <S extends ManagedAttributeDto> S save(S resource) {
    ManagedAttributeDto dto = (ManagedAttributeDto) resource;
    ManagedAttribute managedAttribute = dao.findOneByNaturalId(dto.getUuid(), ManagedAttribute.class);
    mapper.updateManagedAttributeFromDto(dto, managedAttribute);
    dao.save(managedAttribute);
    return resource;
  }

  @Override
  public ManagedAttributeDto findOne(UUID uuid, QuerySpec querySpec) {
    ManagedAttribute managedAttribute = dao.findOneByNaturalId(uuid, ManagedAttribute.class);
    if (managedAttribute == null) {
      // Throw the 404 exception if the resource is not found.
      throw new ResourceNotFoundException(
          this.getClass().getSimpleName() + " with ID " + uuid + " Not Found.");
    }
    return mapper.toDto(managedAttribute);
  }

  @Override
  public ResourceList<ManagedAttributeDto> findAll(QuerySpec querySpec) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <S extends ManagedAttributeDto> S create(S resource) {
    ManagedAttributeDto dto = (ManagedAttributeDto) resource;
    if (dto.getUuid() == null) {
      dto.setUuid(UUID.randomUUID());
    }
    ManagedAttribute managedAttribute = mapper.toEntity((ManagedAttributeDto) resource);
    dao.save(managedAttribute);
    return resource;
  }

  @Override
  public void delete(UUID id) {
    ManagedAttribute managedAttribute = dao.findOneByNaturalId(id, ManagedAttribute.class);
    if (managedAttribute != null) {
      dao.delete(managedAttribute);
    }
  }
}
