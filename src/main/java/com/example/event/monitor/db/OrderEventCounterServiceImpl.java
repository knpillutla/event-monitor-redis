package com.example.event.monitor.db;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.threedsoft.packing.dto.responses.PackResourceDTO;
import com.threedsoft.picking.dto.responses.PickResourceDTO;
import com.threedsoft.util.dto.events.EventResourceConverter;
import com.threedsoft.util.dto.events.WMSEvent;
import com.threedsoft.util.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderEventCounterServiceImpl implements OrderEventCounterService {
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	HashOperations<String, String, Long> redisHashOps = null;
	String orderCountsKey = "OrderCountKey";

	public OrderEventCounterServiceImpl() {
	}

	public OrderEventCounterServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		redisHashOps = redisTemplate.opsForHash();
	}

	@Override
	public void updateOrderCounts(WMSEvent event) {
		log.info("OrderEventCounterServiceImpl::updateOrderCounts::start::" + event.toString());
/*		OrderResourceDTO ordersourceDTO = (OrderResourceDTO) EventResourceConverter
				.getObject(event.getEventResource(), event.getEventResourceClassName());
*/		
		String hourKey = DateTimeUtil.getFormattedStringForCurrentHour();
		
		if (event.getEventName().equalsIgnoreCase("OrderCreatedEvent")) {
			redisHashOps.increment("created"+orderCountsKey + event.getBusName() + event.getLocnNbr(), hourKey, 1);
			redisHashOps.increment("backlog"+orderCountsKey + event.getBusName() + event.getLocnNbr(), hourKey, 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "created", 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "backlog", 1);
		} else if (event.getEventName().equalsIgnoreCase("OrderAllocatedEvent")) {
			redisHashOps.increment("allocated"+orderCountsKey + event.getBusName() + event.getLocnNbr(), hourKey, 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "allocated", 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "created", -1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "backlog", -1);
		} else if (event.getEventName().equalsIgnoreCase("OrderPickedEvent")) {
			redisHashOps.increment("picked"+orderCountsKey + event.getBusName() + event.getLocnNbr(),hourKey, 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "picked", 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "allocated", -1);
		} else if (event.getEventName().equalsIgnoreCase("OrderPackedEvent")) {
			redisHashOps.increment("packed"+orderCountsKey + event.getBusName() + event.getLocnNbr(),hourKey, 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "packed", 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "picked", -1);
		} 
		else if (event.getEventName().equalsIgnoreCase("OrderShippedEvent")) {
			redisHashOps.increment( "shipped"+orderCountsKey + event.getBusName() + event.getLocnNbr(), hourKey, 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "shipped", 1);
			redisHashOps.increment(orderCountsKey + event.getBusName() + event.getLocnNbr(), "packed", -1);
		}
		else if (event.getEventName().equalsIgnoreCase("PickConfirmationEvent")) {
			PickResourceDTO pickResourceDTO = EventResourceConverter.getObject(event.getEventResource(), PickResourceDTO.class);
			redisHashOps.increment( "units-picked"+orderCountsKey + event.getBusName() + event.getLocnNbr(), hourKey, 1);
			redisHashOps.increment( hourKey+"pickers"+orderCountsKey + event.getBusName() + event.getLocnNbr(), pickResourceDTO.getUserId(), pickResourceDTO.getQty());
		}
		else if (event.getEventName().equalsIgnoreCase("PackConfirmationEvent")) {
			PackResourceDTO packResourceDTO = EventResourceConverter.getObject(event.getEventResource(), PackResourceDTO.class);
			redisHashOps.increment( "units-packed"+orderCountsKey + event.getBusName() + event.getLocnNbr(), hourKey, 1);
			redisHashOps.increment( hourKey+"packers"+orderCountsKey + event.getBusName() + event.getLocnNbr(), packResourceDTO.getUserId(), packResourceDTO.getQty());
		}
		else if (event.getEventName().equalsIgnoreCase("ShipConfirmationEvent")) {
			redisHashOps.increment( "units-shipped"+orderCountsKey + event.getBusName() + event.getLocnNbr(), hourKey, 1);
		}
		log.info("OrderEventCounterServiceImpl::updateOrderCounts::completed::" + event.toString());
	}

	@Override
	public CounterStat getOrderCounts(String busName, Integer locnNbr) {
		log.info("OrderEventCounterServiceImpl::getOrderCounts::start");
		long startTime = System.currentTimeMillis();
		Map<String, Long> ordercountsMap = redisHashOps.entries(orderCountsKey + busName + locnNbr);
		CounterStat orderCounterStat = new CounterStat();
		orderCounterStat.setBusName(busName);
		orderCounterStat.setLocnNbr(locnNbr);
		orderCounterStat.setCounterType("OrderCounters");
		orderCounterStat.setCounterDescription("Real Time Order backlog");
		orderCounterStat.setDisplayType(CounterStat.PIECHART_DISPLAY);
		for (Entry<String, Long> entryCounter : ordercountsMap.entrySet()) {
			Counter counter = new Counter();
			//counter.setType("aisle");
			counter.setName(entryCounter.getKey());
			counter.setCount(entryCounter.getValue());
			orderCounterStat.addCounter(counter);
		}

		long endTime = System.currentTimeMillis();
		double totalTime = (endTime - startTime) / 1000.0;
		log.info("OrderEventCounterServiceImpl::getOrderCounts::completed::" + totalTime + " secs :" + orderCounterStat);
		return orderCounterStat;
	}

	@Override
	public Map<String, Map<String, Long>> getOrderHourlyStats(String busName, Integer locnNbr, int numOfDays) {
		Map<String, Map<String, Long>> hourlyOrderCounters = new LinkedHashMap();
		Map<String, Long> hourMap = null;
		hourlyOrderCounters.put("orders created", redisHashOps.entries("created"+orderCountsKey + busName + locnNbr));
		hourlyOrderCounters.put("orders allocated", redisHashOps.entries("allocated"+orderCountsKey + busName + locnNbr));
		hourlyOrderCounters.put("orders picked", redisHashOps.entries("picked"+orderCountsKey + busName + locnNbr));
		hourlyOrderCounters.put("orders packed", redisHashOps.entries("packed"+orderCountsKey + busName + locnNbr));
		hourlyOrderCounters.put("orders shipped", redisHashOps.entries("shipped"+orderCountsKey + busName + locnNbr));
		hourlyOrderCounters.put("units picked", redisHashOps.entries("units-picked"+orderCountsKey + busName + locnNbr));
		hourlyOrderCounters.put("units packed", redisHashOps.entries("units-packed"+orderCountsKey + busName + locnNbr));
		hourlyOrderCounters.put("units shipped", redisHashOps.entries("units-shipped"+orderCountsKey + busName + locnNbr));
		Map<String, Long> pickerCountMap = new LinkedHashMap();
		Map<String, Long> packerCountMap = new LinkedHashMap();
		List<String> formattedHourList = DateTimeUtil.getFormattedHourListFromDate(numOfDays);
		for(String formattedHour : formattedHourList) {
			hourMap = redisHashOps.entries(formattedHour+"pickers"+orderCountsKey + busName + locnNbr);
			pickerCountMap.put(formattedHour, new Long(hourMap.size()));
		}
		
/*		List<LocalDateTime> localDateTimeList = DateTimeUtil.getSortedDateTime(numOfDays);
		for(LocalDateTime dateTime : localDateTimeList) {
			String formattedHour = DateTimeUtil.getFormattedStringForHour(dateTime);
			hourMap = redisHashOps.entries(formattedHour+"pickers"+orderCountsKey + busName + locnNbr);
			pickerCountMap.put(formattedHour, new Long(hourMap.size()));
		}
*/		hourlyOrderCounters.put("Num of Pickers",pickerCountMap);
		hourlyOrderCounters.put("Num of Packers", packerCountMap);
		
		return hourlyOrderCounters;
	}
	
	@Override
	public Map<String, Map<String, Object>> getPickingStats(String busName, Integer locnNbr, Integer numOfDays,int numOfTopPerformers) {
		Map<String, Map<String, Object>> hourlySortedPickerMap = new LinkedHashMap();

		List<String> formattedHourList = DateTimeUtil.getFormattedHourListFromDate(numOfDays);
		for(String formattedHour : formattedHourList) {
			Map<String, Object> statsMap = new LinkedHashMap();
			int numOfPickers = redisHashOps.entries(formattedHour+"pickers"+orderCountsKey + busName + locnNbr).size();
			statsMap.put("Num of Pickers", numOfPickers);
			Long totalPickedUnits = redisHashOps.get("units-picked"+orderCountsKey + busName + locnNbr, formattedHour);
			if(totalPickedUnits == null) totalPickedUnits = 0L;
			Long pickingRate = numOfPickers<=0?0:totalPickedUnits/numOfPickers;
			statsMap.put("Total Picked Units",  totalPickedUnits);
			statsMap.put("Picking Rate",  pickingRate);
			statsMap.put("Top Performers",  getTopPerformers(busName, locnNbr, "pickers", formattedHour, numOfTopPerformers));
			
			// put current hour num of pickers, total picked units, picking rate, all pickers and counts
			statsMap.put("Current Hour Stats",  getTopPerformers(busName, locnNbr, "pickers", formattedHour, numOfTopPerformers));
			hourlySortedPickerMap.put(formattedHour, statsMap);
		}
		return hourlySortedPickerMap;
	}
	
	@Override
	public Map<String, Map<String, Long>> getTopPickPerformersByHourForPast24Hours(String busName, Integer locnNbr, Integer numOfDays,int numOfTopPerformers) {
		Map<String, Map<String, Long>> hourlySortedPickerMap = new LinkedHashMap();

		List<String> formattedHourList = DateTimeUtil.getFormattedHourListFromDate(numOfDays);
		for(String formattedHour : formattedHourList) {
			hourlySortedPickerMap.put(formattedHour, getTopPerformers(busName, locnNbr, "pickers", formattedHour, numOfTopPerformers));
		}
		return hourlySortedPickerMap;
	}
	
	@Override
	public Map<String, Map<String, Long>> getTopPackPerformersByHourForPast24Hours(String busName, Integer locnNbr, Integer numOfDays,int numOfTopPerformers) {
		Map<String, Map<String, Long>> hourlySortedPickerMap = new LinkedHashMap();

		List<String> formattedHourList = DateTimeUtil.getFormattedHourListFromDate(numOfDays);
		for(String formattedHour : formattedHourList) {
			hourlySortedPickerMap.put(formattedHour, getTopPerformers(busName, locnNbr, "packers", formattedHour, numOfTopPerformers));
		}
		return hourlySortedPickerMap;
	}

	@Override
	public Map<String, Map<String, Long>> getTopPickPerformersForCurrentHour(String busName, Integer locnNbr) {
		Map<String, Map<String, Long>> hourlySortedPickerMap = new LinkedHashMap();

		List<String> formattedHourList = DateTimeUtil.getFormattedHourListFromDate(0);
		for(String formattedHour : formattedHourList) {
			hourlySortedPickerMap.put(formattedHour, getTopPerformers(busName, locnNbr, "pickers", formattedHour,-1));
		}
		return hourlySortedPickerMap;
	}

	@Override
	public Map<String, Map<String, Long>> getTopPackPerformersForCurrentHour(String busName, Integer locnNbr) {
		Map<String, Map<String, Long>> hourlySortedPickerMap = new LinkedHashMap();

		List<String> formattedHourList = DateTimeUtil.getFormattedHourListFromDate(0);
		for(String formattedHour : formattedHourList) {
			hourlySortedPickerMap.put(formattedHour, getTopPerformers(busName, locnNbr, "packers", formattedHour,-1));
		}
		return hourlySortedPickerMap;
	}

	private Map<String, Long> getTopPerformers(String busName, Integer locnNbr, String performerType, String formattedHour, int numOfTopPerformers) {
		Map<String, Long> resultMap = new LinkedHashMap();
		Map<String, Long> tmpHourlyPickerMap = redisHashOps.entries(formattedHour+performerType+orderCountsKey + busName + locnNbr);
		Map<String, Long> sortedMap = 
				tmpHourlyPickerMap.entrySet().stream()
			    .sorted( Map.Entry.<String, Long>comparingByValue().reversed())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
		if(numOfTopPerformers<=0) {
			return sortedMap;
		}
		Iterator<Entry<String, Long>> entrySetIterator = sortedMap.entrySet().iterator();
		for(int i=0;i<numOfTopPerformers;i++) {
			if(entrySetIterator.hasNext()) {
				Entry<String, Long> entry = entrySetIterator.next();
				resultMap.put(entry.getKey(), entry.getValue());
			}else {
				break;
			}
		}
		
		return resultMap;
	}
}
