package com.example.event.monitor.db;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.threedsoft.util.dto.events.WMSEvent;

import lombok.extern.slf4j.Slf4j;

public interface PickEventCounterService {
	public void updatePickCounts(WMSEvent event);
	public CounterStat getPickCounts(String busName, Integer locnNbr);
}
