package ca.gc.aafc.objectstore.api.interfaces;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;

public interface SoftDeletableRepository {

  PathSpec DELETED_PATH_SPEC = PathSpec.of(SoftDeletable.DELETED_DATE_FIELD_NAME);

  FilterSpec DELETED_DATE_IS_NULL = new FilterSpec(DELETED_PATH_SPEC, FilterOperator.EQ, null);

}