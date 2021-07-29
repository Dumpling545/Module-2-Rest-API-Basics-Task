package com.epam.esm.web;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.epam.esm.web.ResourcePaths.ROOT;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller handling get request to root endpoints
 * which provides information about all endpoints (for REST discoverability)
 */
@RestController
@RequestMapping(ROOT)
public class RootController {
    private static final String TAGS = ResourcePaths.TAGS.substring(1);
    private static final String GIFT_CERTIFICATES = ResourcePaths.GIFT_CERTIFICATES.substring(1);
    private static final String ORDERS = ResourcePaths.ORDERS.substring(1);
    private static final String USERS = ResourcePaths.USERS.substring(1);
    private static final String MOST_WIDELY_USED = "most-widely-used";

    @GetMapping
    public ResponseEntity<RepresentationModel> getApi() {
        RepresentationModel model = new RepresentationModel();
        Link tagItem = linkTo(methodOn(TagController.class).getTag(null))
                .withRel(TAGS + '-' + IanaLinkRelations.ITEM_VALUE);
        Link tagCollection = linkTo(methodOn(TagController.class).allTags(null, null))
                .withRel(TAGS + '-' + IanaLinkRelations.COLLECTION_VALUE);
        Link tagMostWidelyUsed = linkTo(methodOn(TagController.class)
                .getMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(null))
                .withRel(TAGS + '-' + MOST_WIDELY_USED);

        Link giftCertificateItem = linkTo(methodOn(GiftCertificateController.class).getCertificate(null))
                .withRel(GIFT_CERTIFICATES + '-' + IanaLinkRelations.ITEM_VALUE);
        Link giftCertificateCollection = linkTo(methodOn(GiftCertificateController.class)
                .filteredCertificates(null, null, null, null, null, null))
                .withRel(GIFT_CERTIFICATES + '-' + IanaLinkRelations.COLLECTION_VALUE);

        Link userItem = linkTo(methodOn(UserController.class).getUser(null))
                .withRel(USERS + '-' + IanaLinkRelations.ITEM_VALUE);
        Link userOrders = linkTo(methodOn(UserController.class).getOrdersByUser(null, null, null))
                .withRel(USERS + '-' + ORDERS);
        Link userCollection = linkTo(methodOn(UserController.class).allUsers(null, null))
                .withRel(USERS + '-' + IanaLinkRelations.COLLECTION_VALUE);

        Link orderItem = linkTo(methodOn(OrderController.class).getOrder(null))
                .withRel(ORDERS + '-' + IanaLinkRelations.ITEM_VALUE);
        Link orderCollection = linkTo(methodOn(OrderController.class).allOrders(null, null))
                .withRel(ORDERS + '-' + IanaLinkRelations.COLLECTION_VALUE);

        Link selfLink = linkTo(methodOn(RootController.class).getApi()).withSelfRel();
        model.add(tagItem, tagCollection, tagMostWidelyUsed,
                giftCertificateItem, giftCertificateCollection,
                userItem, userOrders, userCollection,
                orderItem, orderCollection,
                selfLink);
        return ResponseEntity.ok(model);
    }
}
