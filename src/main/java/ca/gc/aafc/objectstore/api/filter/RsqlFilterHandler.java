package ca.gc.aafc.objectstore.api.filter;

import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import com.github.tennaito.rsql.jpa.JpaPredicateVisitor;

import org.apache.commons.lang3.StringUtils;

import cz.jirutka.rsql.parser.RSQLParser;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.data.jpa.internal.query.backend.criteria.JpaCriteriaQueryExecutorImpl;
import io.crnk.data.jpa.query.JpaQueryExecutor;
import lombok.RequiredArgsConstructor;

@Named
// CHECKSTYLE:OFF AnnotationUseStyle
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RsqlFilterHandler {

  private final EntityManager entityManager;
  private final RSQLParser rsqlParser = new RSQLParser();

  /**
   * Using this method to apply an RSQL filter to a crnk-jpa query takes three steps in order:
   * 
   * 1. Call "getRestrictionApplier" by passing in the QuerySpec before calling JpaCriteriaQuery#buildExecutor.
   * 2. Get the JpaQueryExecutor by calling JpaCriteriaQuery#buildExecutor.
   * 3. Call the returned Consumer#accept by passing in the JpaQueryExecutor.
   */
  public Consumer<JpaQueryExecutor<?>> getRestrictionApplier(QuerySpec querySpec) {
    FilterSpec rsqlFilterSpec = querySpec.findFilter(PathSpec.of("rsql")).orElse(null);
    // Remove the rsqlFilterSpec from the filters list so Crnk-jpa doesn't throw an
    // error by
    // assuming the filter[rsql] param refers to an "rsql" field on the Entity.
    if (rsqlFilterSpec != null) {
      querySpec.getFilters().remove(rsqlFilterSpec);
    }

    return executor -> {
      JpaCriteriaQueryExecutorImpl<?> executorImpl = (JpaCriteriaQueryExecutorImpl<?>) executor;
      CriteriaQuery<?> query = executorImpl.getQuery();
      CriteriaBuilder cb = entityManager.getCriteriaBuilder();

      
      if (rsqlFilterSpec == null || StringUtils.isBlank(rsqlFilterSpec.getValue().toString())) {
        // Do nothing if there is no requested RSQL filter.
        return;
      }
      
      String rsqlString = rsqlFilterSpec.getValue();
      
      Predicate existingRestriction = Optional.ofNullable(query.getRestriction()).orElse(cb.and());
      Predicate rsqlRestriction = rsqlParser.parse(rsqlString)
        .accept(
          new JpaPredicateVisitor<>().defineRoot(query.getRoots().iterator().next()),
          entityManager
        );

      query.where(cb.and(existingRestriction, rsqlRestriction));
    };

  }

}