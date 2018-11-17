package com.example.event.monitor.streams;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface EventMonitorStreams {
	public String CUSTOMER_ORDERS_OUTPUT = "customer-orders-out";
	public String ORDERS_OUTPUT = "orders-out";
    public String PICK_OUTPUT = "pick-out";
    public String PACK_OUTPUT = "pack-out";
    public String INVENTORY_OUTPUT = "inventory-out";
    public String SHIP_OUTPUT = "ship-out";

    @Input(CUSTOMER_ORDERS_OUTPUT)
    public SubscribableChannel outboundCustomerOrders();

    @Input(ORDERS_OUTPUT)
    public SubscribableChannel outboundOrders();

    @Input(PICK_OUTPUT)
    public SubscribableChannel outboundPicks();

    @Input(PACK_OUTPUT)
    public SubscribableChannel outboundPacks();
   
    @Input(INVENTORY_OUTPUT)
    public SubscribableChannel outboundInventory();

    @Input(SHIP_OUTPUT)
    public SubscribableChannel outboundShip();
}