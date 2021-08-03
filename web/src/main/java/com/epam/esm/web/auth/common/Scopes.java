package com.epam.esm.web.auth.common;

/**
 * Class-container for all OAuth2 scopes and their distribution among user roles
 */
public final class Scopes {
	private Scopes() {
	}

	public static final String GIFT_CERTIFICATES_READ = "gift-certificates:read";
	public static final String GIFT_CERTIFICATES_WRITE = "gift-certificates:write";
	public static final String ORDERS_READ = "orders:read";
	public static final String ORDERS_WRITE_OTHERS = "orders:write-others";
	public static final String ORDERS_WRITE_SELF = "orders:write-self";
	public static final String ROOT_READ = "root:read";
	public static final String TAGS_READ = "tags:read";
	public static final String TAGS_WRITE = "tags:write";
	public static final String USERS_READ = "users:read";
	public static final String USERS_WRITE_NEW = "users:write-new";
	public static final String[] GUEST_SCOPES = {ROOT_READ, USERS_WRITE_NEW, GIFT_CERTIFICATES_READ};
	public static final String[] USER_SCOPES = {ROOT_READ, USERS_READ, USERS_WRITE_NEW, GIFT_CERTIFICATES_READ,
	                                            TAGS_READ, ORDERS_READ, ORDERS_WRITE_SELF};
	public static final String[] ADMIN_SCOPES = {ROOT_READ, USERS_READ, USERS_WRITE_NEW, GIFT_CERTIFICATES_READ,
	                                             GIFT_CERTIFICATES_WRITE, TAGS_READ, TAGS_WRITE, ORDERS_READ,
	                                             ORDERS_WRITE_SELF, ORDERS_WRITE_OTHERS};
}
