package com.epam.esm.db.helper;

import com.epam.esm.model.entity.SortOption;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;


/**
 * Helper interface aimed to construct {@link Order} from {@link SortOption}
 */
public interface OrderHelper<R> {
	Order createOrder(SortOption sortOption, CriteriaBuilder criteriaBuilder, Root<R> root);
}
