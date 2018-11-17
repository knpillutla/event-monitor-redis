package com.example.event.monitor.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.event.monitor.db.EventCounterService;
import com.threedsoft.util.dto.events.WMSEvent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventMonitorService {

	@Autowired
	MessagePublisher redisMessagePublisher;
	
	@Autowired
	EventCounterService eventCounterService;

	public void add(WMSEvent wmsEvent) {
		log.info("Received msg and publishing to redis channel:" + wmsEvent.getEventName());
		redisMessagePublisher.publish(wmsEvent);
		log.info("published to redis channel:" + wmsEvent.getEventName());
	}
	
	public Map<String,Map<String, Long>> getEventCounters(String busName, Integer locnNbr, Integer numOfDays) {
		 return eventCounterService.getEventCounters(numOfDays);
	}
}
