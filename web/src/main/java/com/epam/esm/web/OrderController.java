package com.epam.esm.web;

import com.epam.esm.model.dto.OrderDTO;
import com.epam.esm.model.dto.PageDTO;
import com.epam.esm.model.dto.PagedResultDTO;
import com.epam.esm.service.OrderService;
import com.epam.esm.web.assembler.ExtendedRepresentationModelAssembler;
import com.epam.esm.web.auth.common.Scopes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_NUMBER;
import static com.epam.esm.model.dto.ValidationConstraints.MIN_PAGE_SIZE;
import static com.epam.esm.web.ResourcePaths.ORDERS;

/**
 * Controller handling requests to 'order' resource
 */
@RestController
@RequestMapping(ORDERS)
@RequiredArgsConstructor
public class OrderController {
    @Value("${oauth2.claims.user-id}")
    private String userIdClaimName;
    private final OrderService orderService;
    private final ExtendedRepresentationModelAssembler<OrderDTO, OrderController> assembler;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel> getOrder(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(assembler.toModel(orderService.getOrder(id)));
    }

    @GetMapping
    public ResponseEntity<CollectionModel> allOrders(@RequestParam(defaultValue = MIN_PAGE_NUMBER + "")
                                                             Integer pageNumber,
                                                     @RequestParam(defaultValue = MIN_PAGE_SIZE + "")
                                                             Integer pageSize) {
        PageDTO pageDTO = PageDTO.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
        PagedResultDTO<OrderDTO> pagedResultDTO = orderService.getAllOrders(pageDTO);
        CollectionModel<EntityModel<OrderDTO>> model =
                assembler.toPagedCollectionModel(pageNumber, pagedResultDTO,
                        (c, p) -> c.allOrders(p, pageSize));
        return ResponseEntity.ok(model);
    }

    @PostMapping
    @PreAuthorize("hasPermission(#orderDTO, 'CREATE')")
    public ResponseEntity createOrder(Authentication authentication,
                                      @RequestBody @Valid OrderDTO orderDTO, UriComponentsBuilder ucb) {
        if (orderDTO.getUserId() == null) {
            Jwt jwtToken = (Jwt) authentication.getPrincipal();
            int userId = jwtToken.<Long>getClaim(userIdClaimName).intValue();
            orderDTO.setUserId(userId);
        }
        OrderDTO dto = orderService.createOrder(orderDTO);
        URI locationUri = ucb.path("/orders/").path(String.valueOf(dto.getId())).build().toUri();
        return ResponseEntity.created(locationUri).build();
    }
}
