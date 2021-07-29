package com.epam.esm.service.converter;

import com.epam.esm.model.dto.GiftCertificateOutputDTO;
import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.model.dto.TagDTO;
import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.GiftCertificate;
import com.epam.esm.model.entity.Order;
import com.epam.esm.model.entity.PagedResult;
import com.epam.esm.model.entity.Tag;
import com.epam.esm.model.entity.User;
import org.mapstruct.Mapper;

/**
 * Interface mapping pagedResult (containing retrieved paged list and boolean flags)
 * entities and DTOs. Used by MapStruct to generate actual mapper class
 *
 * @see <a href="https://mapstruct.org/">MapStruct library</a>
 */
@Mapper(componentModel = "spring")
public interface PagedResultConverter {
    PagedResultDTO<GiftCertificateOutputDTO> convertToCertificatePage(PagedResult<GiftCertificate> pagedResult);

    PagedResultDTO<TagDTO> convertToTagPage(PagedResult<Tag> pagedResult);

    PagedResultDTO<OrderDTO> convertToOrderPage(PagedResult<Order> pagedResult);

    PagedResultDTO<UserDTO> convertToUserPage(PagedResult<User> pagedResult);
}
