package ca.gc.aafc.objectstore.api.respository.auditlog;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.dina.repository.NoLinkInformation;
import ca.gc.aafc.objectstore.api.dto.AuditSnapshotDto;
import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;

@Repository
public class AuditSnapshotRepository extends ReadOnlyResourceRepositoryBase<AuditSnapshotDto, Long> {
  private final Javers javers;

  public AuditSnapshotRepository(Javers javers) {
    super(AuditSnapshotDto.class);
    this.javers = javers;
  }

  @Override
  public ResourceList<AuditSnapshotDto> findAll(QuerySpec qs) {
    QueryBuilder queryBuilder;

    // Check if the client is requesting logs of a specific record:
    Optional<FilterSpec> instanceFilter = qs.findFilter(PathSpec.of("instanceId"));
    if (instanceFilter.isPresent()) {
      // Allow filtering by instance:
      String instanceId = instanceFilter.get().getValue();
      String[] split = instanceId.split("/");
      if (split.length != 2) {
        throw new IllegalArgumentException("Invalid ID must be formatted as {type}/{id}: " + instanceId);
      }
      String type = split[0];
      String id = split[1];
      queryBuilder = QueryBuilder.byInstanceId(id, type);
    } else {
      // Allow filtering by any object:
      queryBuilder = QueryBuilder.anyDomainObject();
    }

    // Allow filter by author:
    qs.findFilter(PathSpec.of("author")).ifPresent(
      it -> queryBuilder.byAuthor(it.getValue())
    );

    // Set paging limit and offset:
    queryBuilder.limit(Optional.ofNullable(qs.getLimit()).orElse(100L).intValue());
    queryBuilder.skip(Optional.ofNullable(qs.getOffset()).orElse(0L).intValue());
    
    JqlQuery query = queryBuilder.build();

    // Execute the query:
    List<CdoSnapshot> javersSnapshots = javers.findSnapshots(query);

    // Convert to DTOs:
    List<AuditSnapshotDto> dtos = javersSnapshots.stream().map(this::toDto).collect(Collectors.toList());

    return new DefaultResourceList<>(dtos, null, new NoLinkInformation());
  }

  @Override
  public AuditSnapshotDto findOne(Long id, QuerySpec querySpec) {
    throw new MethodNotAllowedException("method not allowed");
  }

  /** Converts Javers snapshot to our DTO format. */
  private AuditSnapshotDto toDto(CdoSnapshot original) {
    // Get the snapshot state as a map:
    Map<String, Object> state = new HashMap<>();
    original.getState().forEachProperty((key, val) -> state.put(key, val));

    // Get the commit date as OffsetDateTime:
    OffsetDateTime commitDateTime = original.getCommitMetadata().getCommitDate().atOffset(OffsetDateTime.now().getOffset());

    return AuditSnapshotDto.builder()
        .id(original.getGlobalId().value() + "/" + commitDateTime)   
        .instanceId(original.getGlobalId().value())
        .state(state)
        .changedProperties(original.getChanged())
        .snapshotType(original.getType().toString())
        .version(original.getVersion())
        .author(original.getCommitMetadata().getAuthor())
        .commitDateTime(commitDateTime)
        .build();
  }

}
