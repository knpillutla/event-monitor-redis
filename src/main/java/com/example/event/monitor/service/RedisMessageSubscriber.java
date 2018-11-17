package com.example.event.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.example.event.monitor.db.EventCounterService;
import com.example.event.monitor.db.OrderEventCounterService;
import com.example.event.monitor.db.PickEventCounterService;
import com.threedsoft.util.dto.events.EventResourceConverter;
import com.threedsoft.util.dto.events.WMSEvent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

	// public static List<String> messageList = new ArrayList<String>();

	@Autowired
	EventCounterService eventCounterService;

	@Autowired
	PickEventCounterService pickCounterService;

	@Autowired
	OrderEventCounterService orderCounterService;

	public RedisMessageSubscriber(EventCounterService eventCounterService, PickEventCounterService pickCounterService,
			OrderEventCounterService orderCounterService) {
		this.eventCounterService = eventCounterService;
		this.pickCounterService = pickCounterService;
		this.orderCounterService = orderCounterService;
	}

	public void onMessage(Message message, byte[] pattern) {
		// messageList.add(message.toString());
		WMSEvent wmsEvent;
		// GenericJackson2JsonRedisSerializer deSerializer = new
		// GenericJackson2JsonRedisSerializer();
		// wmsEvent = (WMSEvent) deSerializer.deserialize(message.getBody(),
		// WMSEvent.class);
		wmsEvent = (WMSEvent) EventResourceConverter.getObject(message.getBody(), WMSEvent.class);
		log.info("Event received: " + wmsEvent);
		if (eventCounterService == null) {
			log.error("Event Counter Service is not initialized");
		} else {
			// eventStatisticsRepository.addNewEvent(wmsEvent);
			eventCounterService.addNewEvent(wmsEvent.getEventName());
			if (wmsEvent.getEventName().toLowerCase().startsWith("pick")) {
				pickCounterService.updatePickCounts(wmsEvent);
			}
			if (wmsEvent.getEventName().toLowerCase().startsWith("order")
					|| wmsEvent.getEventName().equals("PickConfirmationEvent")
					|| wmsEvent.getEventName().equals("PackConfirmationEvent")) {
				orderCounterService.updateOrderCounts(wmsEvent);
			}

			log.info("Get EventCounters for current hour: " + eventCounterService.getCurrentHourEventCounters());
		}
	}
}