package com.example.event.monitor.endpoint.listener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.example.event.monitor.service.EventMonitorService;
import com.example.event.monitor.streams.EventMonitorStreams;
import com.threedsoft.util.dto.events.WMSEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventMonitorListener {
	@Autowired
	EventMonitorService eventMonitorService;

/*	@StreamListener(target = OrderAggregatorStreams.ORDERS_OUTPUT)
	public void handleNewOrder(Message msg) {
		log.info("Received order Msg:" + msg.getPayload() + ": at :" + LocalDateTime.now());
		long startTime = System.currentTimeMillis();
		try {
			eventMonitorService.add((BaseEvent)msg.getPayload());
			long endTime = System.currentTimeMillis();
			log.info("Completed order event for : " + msg.getPayload() + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing order event for : " + msg.getPayload() + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}*/
	@StreamListener(target = EventMonitorStreams.CUSTOMER_ORDERS_OUTPUT)
	public void handleCustomerOrderEvents(WMSEvent event) {
		log.info("Received order Msg:" + event + ": at :" + LocalDateTime.now());
		long startTime = System.currentTimeMillis();
		try {
			eventMonitorService.add(event);
			long endTime = System.currentTimeMillis();
			log.info("Completed order event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing order event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

	@StreamListener(target = EventMonitorStreams.ORDERS_OUTPUT)
	public void handleOrderEvents(WMSEvent event) {
		log.info("Received order Msg:" + event + ": at :" + LocalDateTime.now());
		long startTime = System.currentTimeMillis();
		try {
			eventMonitorService.add(event);
			long endTime = System.currentTimeMillis();
			log.info("Completed order event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing order event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

	@StreamListener(target = EventMonitorStreams.INVENTORY_OUTPUT)
	public void handleInventoryEvents(WMSEvent event) {
		log.info("Received inventory Msg:" + event + ": at :" + LocalDateTime.now());
		long startTime = System.currentTimeMillis();
		try {
			eventMonitorService.add(event);
			long endTime = System.currentTimeMillis();
			log.info("Completed inventory event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing inventory event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}
	
	@StreamListener(target = EventMonitorStreams.PICK_OUTPUT)
	public void handlePickEvents(WMSEvent event) {
		log.info("Received pick Msg:" + event + ": at :" + LocalDateTime.now());
		long startTime = System.currentTimeMillis();
		try {
			eventMonitorService.add(event);
			long endTime = System.currentTimeMillis();
			log.info("Completed pick event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing pick event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}
	
	@StreamListener(target = EventMonitorStreams.PACK_OUTPUT)
	public void handlePackEvents(WMSEvent event) {
		log.info("Received pack Msg:" + event + ": at :" + LocalDateTime.now());
		long startTime = System.currentTimeMillis();
		try {
			eventMonitorService.add(event);
			long endTime = System.currentTimeMillis();
			log.info("Completed pack event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing pack event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}
	@StreamListener(target = EventMonitorStreams.SHIP_OUTPUT)
	public void handleShipEvents(WMSEvent event) {
		log.info("Received ship Msg:" + event + ": at :" + LocalDateTime.now());
		long startTime = System.currentTimeMillis();
		try {
			eventMonitorService.add(event);
			long endTime = System.currentTimeMillis();
			log.info("Completed ship event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing ship event for : " + event + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}	
}
