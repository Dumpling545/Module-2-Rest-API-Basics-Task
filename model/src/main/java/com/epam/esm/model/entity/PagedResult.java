package com.epam.esm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Entity object encapsulating resulting list and pagination info.
 * Used for Service layer <- Repository layer communication
 */
@Data
@Builder
@AllArgsConstructor
public class PagedResult<T> {
	private List<T> page;
	private boolean first;
	private boolean last;
}
