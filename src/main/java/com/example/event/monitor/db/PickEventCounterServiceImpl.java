package com.example.event.monitor.db;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.threedsoft.picking.dto.responses.PickResourceDTO;
import com.threedsoft.util.dto.events.EventResourceConverter;
import com.threedsoft.util.dto.events.WMSEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PickEventCounterServiceImpl implements PickEventCounterService{
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	HashOperations<String, String, Long> redisHashOps = null;
	String pickCountsKey = "PickCount";
	
	public PickEventCounterServiceImpl() {
	}

	public PickEventCounterServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		redisHashOps = redisTemplate.opsForHash();
	}

	
	public void updatePickCounts(WMSEvent event) {
		log.info("PickEventCounterServiceImpl::updatePickCounts::start::" + event.toString());
		PickResourceDTO pickResourceDTO = EventResourceConverter.getObject(event.getEventResource(), PickResourceDTO.class);
		String locnBrcd = pickResourceDTO.getLocnBrcd();
		String area = locnBrcd.substring(0, 1);
		String zone = locnBrcd.substring(0,2);
		String aisle = locnBrcd.substring(0,4);
		if(event.getEventName().equalsIgnoreCase("PickCreatedEvent")) {
			redisHashOps.increment(pickCountsKey+event.getBusName() + event.getLocnNbr()+"area", area, new Long(pickResourceDTO.getQty()));
			redisHashOps.increment(pickCountsKey+event.getBusName() + event.getLocnNbr()+"zone", zone, new Long(pickResourceDTO.getQty()));
			redisHashOps.increment(pickCountsKey+event.getBusName() + event.getLocnNbr()+"aisle", aisle, new Long(pickResourceDTO.getQty()));
		}else if(event.getEventName().equalsIgnoreCase("PickConfirmationEvent")) {
			redisHashOps.increment(pickCountsKey+event.getBusName() + event.getLocnNbr()+"area", area, new Long(-pickResourceDTO.getQty()));
			redisHashOps.increment(pickCountsKey+event.getBusName() + event.getLocnNbr()+"zone", zone, new Long(-pickResourceDTO.getQty()));
			redisHashOps.increment(pickCountsKey+event.getBusName() + event.getLocnNbr()+"aisle", aisle, new Long(-pickResourceDTO.getQty()));
		}
		log.info("PickEventCounterServiceImpl::updatePickCounts::completed::" + event.toString());
	}
	 
	@Override
	public CounterStat getPickCounts(String busName, Integer locnNbr) {
		log.info("PickEventCounterServiceImpl::getPickCounts::start");
		long startTime = System.currentTimeMillis();
		Map<String, Long> pickCountsAreaMap = redisHashOps.entries(pickCountsKey+busName + locnNbr+"area");
		CounterStat pickHeatMapCounterStat = new CounterStat();
		pickHeatMapCounterStat.setBusName(busName);
		pickHeatMapCounterStat.setLocnNbr(locnNbr);
		pickHeatMapCounterStat.setCounterType("Pick Heat Map Counters areas/zones/aisles");
		pickHeatMapCounterStat.setDisplayType(CounterStat.HEATMAP_DISPLAY);
		for(Entry<String, Long> entryArea: pickCountsAreaMap.entrySet())
		{
			Counter areaCounter = new Counter();
			areaCounter.setType("area");
			areaCounter.setName(entryArea.getKey());
			areaCounter.setAltName(entryArea.getKey());
			areaCounter.setCount(entryArea.getValue());
			Map<String, Long> pickCountsZoneMap = redisHashOps.entries(pickCountsKey+busName + locnNbr+"zone");
			for(Entry<String, Long> entryZone: pickCountsZoneMap.entrySet())
			{
				if(entryZone.getKey().startsWith(areaCounter.getAltName())) {
					Counter zoneCounter = new Counter();
					zoneCounter.setType("zone");
					zoneCounter.setName(entryZone.getKey().substring(1, 2));
					zoneCounter.setAltName(entryZone.getKey());
					zoneCounter.setCount(entryZone.getValue());
					Map<String, Long> pickCountsAisleMap = redisHashOps.entries(pickCountsKey+busName + locnNbr+"aisle");
					for(Entry<String, Long> entryAisle: pickCountsAisleMap.entrySet())
					{
						if(entryAisle.getKey().startsWith(zoneCounter.getAltName())) {
							Counter aisleCounter = new Counter();
							aisleCounter.setType("aisle");
							aisleCounter.setName(entryAisle.getKey().substring(2, 4));
							aisleCounter.setAltName(entryAisle.getKey());
							aisleCounter.setCount(entryAisle.getValue());
							zoneCounter.addCounter(aisleCounter);
						}
					}
					areaCounter.addCounter(zoneCounter);
				}
			}
			pickHeatMapCounterStat.addCounter(areaCounter);
		}
		long endTime = System.currentTimeMillis();
		double totalTime = (endTime-startTime)/1000.0;
		log.info("PickEventCounterServiceImpl::getPickCounts::completed::" + totalTime + " secs :" + pickHeatMapCounterStat);
		return pickHeatMapCounterStat;
	}
	
	
}
