package com.example.event.monitor.db;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.threedsoft.util.dto.events.WMSEvent;

import lombok.extern.slf4j.Slf4j;

public interface OrderEventCounterService {
	CounterStat getOrderCounts(String busName, Integer locnNbr);

	void updateOrderCounts(WMSEvent event);

	Map<String, Map<String, Long>> getOrderHourlyStats(String busName, Integer locnNbr, int numOfDays);

	Map<String, Map<String, Long>> getTopPickPerformersByHourForPast24Hours(String busName, Integer locnNbr,
			Integer numOfDays, int numOfTopPerformers);

	Map<String, Map<String, Long>> getTopPackPerformersByHourForPast24Hours(String busName, Integer locnNbr,
			Integer numOfDays, int numOfTopPerformers);

	Map<String, Map<String, Long>> getTopPickPerformersForCurrentHour(String busName, Integer locnNbr);

	Map<String, Map<String, Long>> getTopPackPerformersForCurrentHour(String busName, Integer locnNbr);

	Map<String, Map<String, Object>> getPickingStats(String busName, Integer locnNbr, Integer numOfDays,
			int numOfTopPerformers);
}
