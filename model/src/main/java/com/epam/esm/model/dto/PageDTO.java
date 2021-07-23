package com.epam.esm.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.epam.esm.model.dto.ValidationConstraints.MAX_PAGE_SIZE;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_NUMBER;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_SIZE;

/**
 * DTO object encapsulating information about page for any GET endpoints that should return lists.
 * Used for Web Layer -> Service layer communication
 */
@Data
@Builder
public class PageDTO {
	@NotNull(message = "{page.validation-message.page-number-not-empty}")
	@Min(value = MIN_PAGE_NUMBER, message = "{page.validation-message.page-number-min}")
	private Integer pageNumber;
	@NotNull(message = "{page.validation-message.page-size-not-empty}")
	@Min(value = MIN_PAGE_SIZE, message = "{page.validation-message.page-size-min}")
	@Max(value = MAX_PAGE_SIZE, message = "{page.validation-message.page-size-max}")
	private Integer pageSize;

	public int getOffset(){
		return (pageNumber - 1) * pageSize;
	}
}
