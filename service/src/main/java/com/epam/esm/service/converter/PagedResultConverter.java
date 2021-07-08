package com.epam.esm.service.converter;

import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.entity.PagedResult;

import java.util.function.Function;

public interface PagedResultConverter<S, D> {
	PagedResultDTO<D> convert(PagedResult<S> pagedResult, PageDTO pageDTO, Function<S, D> converter);
}
