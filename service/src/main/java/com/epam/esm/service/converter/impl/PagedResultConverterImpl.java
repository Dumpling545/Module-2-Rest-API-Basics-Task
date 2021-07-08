package com.epam.esm.service.converter.impl;

import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.service.converter.PagedResultConverter;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PagedResultConverterImpl<S, D> implements PagedResultConverter<S, D> {
	@Override
	public PagedResultDTO<D> convert(PagedResult<S> pagedResult, PageDTO pageDTO, Function<S, D> converter) {
		PagedResultDTO.PagedResultDTOBuilder<D> builder = PagedResultDTO.<D>builder()
				.previousPageNumber(pagedResult.isFirst() ? null : pageDTO.getPageNumber() - 1)
				.nextPageNumber(pagedResult.isLast() ? null : pageDTO.getPageNumber() + 1)
				.page(pagedResult.getPage().stream().map(converter).toList());
		return builder.build();
	}
}
