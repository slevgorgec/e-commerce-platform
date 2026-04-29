package com.n11bootcamp.ecommerce.common.event;

public final class EventRoutingKeys {

    private EventRoutingKeys() {}

    public static final String EXCHANGE = "ecommerce.events";
    public static final String DEAD_LETTER_EXCHANGE = "ecommerce.events.dlx";

    public static final String USER_REGISTERED = "user.registered";

    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String ORDER_CANCELLED = "order.cancelled";

    public static final String STOCK_RESERVED = "stock.reserved";
    public static final String STOCK_RESERVATION_FAILED = "stock.reservation.failed";
    public static final String STOCK_COMMITTED = "stock.committed";
    public static final String STOCK_RELEASED = "stock.released";

    public static final String PAYMENT_REQUESTED = "payment.requested";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED = "payment.failed";
}