package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResultDTO<T> {
	private Integer previousPageNumber;
	private Integer nextPageNumber;
	private List<T> page;
}
