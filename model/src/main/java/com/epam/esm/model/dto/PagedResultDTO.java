package com.epam.esm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO object encapsulating resulting list for any service method
 * and pagination info. Used for Web Layer <- Service layer communication
 */
@Data
@Builder
@AllArgsConstructor
public class PagedResultDTO<T> {
	private boolean first;
	private boolean last;
	private List<T> page;
}
