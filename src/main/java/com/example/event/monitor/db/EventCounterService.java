package com.example.event.monitor.db;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.threedsoft.util.dto.events.WMSEvent;

import lombok.extern.slf4j.Slf4j;

public interface EventCounterService {
	public void addNewEvent(WMSEvent event);
	void addNewEvent(String eventName);
	Map<String, Map<String, Long>> getCurrentHourEventCounters();
	Map<String, Map<String, Long>> getEventCounters(int numOfDays);
}
