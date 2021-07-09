package com.epam.esm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PagedResultDTO<T> {
	private boolean first;
	private boolean last;
	private List<T> page;
}
