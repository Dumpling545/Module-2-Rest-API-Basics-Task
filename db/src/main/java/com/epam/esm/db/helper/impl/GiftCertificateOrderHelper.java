package com.epam.esm.db.helper.impl;

import com.epam.esm.db.helper.OrderHelper;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.GiftCertificate_;
import com.epam.esm.model.entity.SortOption;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.function.Function;

@Component
public class GiftCertificateOrderHelper implements OrderHelper<GiftCertificate> {
	@Override
	public Order createOrder(SortOption sortOption, CriteriaBuilder criteriaBuilder, Root<GiftCertificate> root) {
		Function<Expression<?>, Order> direction = switch (sortOption.getDirection()) {
			case ASC -> criteriaBuilder::asc;
			case DESC -> criteriaBuilder::desc;
		};
		Path<?> sortCriteria = switch (sortOption.getField()) {
			case NAME -> root.get(GiftCertificate_.name);
			case CREATE_DATE -> root.get(GiftCertificate_.createDate);
			case LAST_UPDATE_DATE -> root.get(GiftCertificate_.lastUpdateDate);
		};
		return direction.apply(sortCriteria);
	}
}
