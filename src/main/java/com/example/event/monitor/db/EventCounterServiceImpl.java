package com.example.event.monitor.db;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.threedsoft.util.dto.events.WMSEvent;
import com.threedsoft.util.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventCounterServiceImpl implements EventCounterService{
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	HashOperations<String, String, Long> redisHashOps = null;
	
	public EventCounterServiceImpl() {
	}

	public EventCounterServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		redisHashOps = redisTemplate.opsForHash();
	}

	@Override
	public void addNewEvent(String eventName) {
		updateEventCount(eventName);
	}
	
	public void updateEventCount(String eventName) {
		String hourKey = DateTimeUtil.getFormattedStringForCurrentHour();
		Long eventCount = redisHashOps.get(hourKey, eventName);
		Long incrementVal = 1L;
		if (eventCount == null) {
			log.info("HashKey do not exist for :" + hourKey + ":" + eventName + ": adding new hash entry");
			redisHashOps.put(hourKey, eventName, incrementVal);
		} else {
			log.info("Number of entries in befpre adding for key:" + hourKey + ": count : " +  eventCount + ": current event:" + eventName);
			redisHashOps.increment(hourKey, eventName, incrementVal);
			log.info("Number of entries in after adding for key:" + hourKey + ": count:" + redisHashOps.get(hourKey, eventName));
		}
	}

	@Override
	public Map<String, Map<String, Long>> getCurrentHourEventCounters() {
		Map<String, Map<String, Long>> currentHourCounters = new HashMap();
		String hourKey = DateTimeUtil.getFormattedStringForCurrentHour();
		Map<String, Long> hourMap = redisHashOps.entries(hourKey);
		currentHourCounters.put(hourKey, hourMap);
		return currentHourCounters;
	}	
	
	@Override
	public Map<String, Map<String, Long>> getEventCounters(int numOfDays) {
		Map<String, Map<String, Long>> eventCounters = new HashMap();
		Map<String, Long> hourMap;
		List<String> formattedHourList = DateTimeUtil.getFormattedHourListFromDate(numOfDays);
		for(String formattedHour : formattedHourList) {
			hourMap = redisHashOps.entries(formattedHour);
			eventCounters.put(formattedHour, hourMap);
		}
		return eventCounters;
	}
	

	@Override
	public void addNewEvent(WMSEvent event) {
		this.addNewEvent(event.getEventName());
	}	
}
