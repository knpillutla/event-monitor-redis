package com.example.event.monitor.endpoint.rest;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.event.monitor.db.CounterStat;
import com.example.event.monitor.db.OrderEventCounterService;
import com.example.event.monitor.db.PickEventCounterService;
import com.example.event.monitor.service.EventMonitorService;
import com.threedsoft.util.dto.ErrorResourceDTO;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/eventmonitor/v1")
@Api(value = "Event Monitor Service", description = "Operations pertaining to Monitoring of Events")
@RefreshScope
@Slf4j
public class EventMonitorRestEndPoint {
	@Autowired
	EventMonitorService eventMonitorService;

	@Autowired
	OrderEventCounterService orderCounterService;

	@Value("${wms.service.health.msg: Event Monitor Service - Config Server is not working..please check}")
	private String healthMsg;

	@Value("${wms.service.ready.msg: Event Monitor Service - Not ready yet}")
	private String readyMsg;

	@GetMapping("/ready")
	public ResponseEntity ready() throws Exception {
		return ResponseEntity.ok(readyMsg);
	}

	@GetMapping("/health")
	public ResponseEntity health() throws Exception {
		return ResponseEntity.ok(healthMsg);
	}

	@GetMapping("/{busName}/{locnNbr}/eventcounters/{numOfDays}")
	public ResponseEntity getById(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr,
			@PathVariable("numOfDays") Integer numOfDays) throws IOException {
		try {
			return ResponseEntity.ok(eventMonitorService.getEventCounters(busName, locnNbr, numOfDays));
		} catch (Exception e) {
			log.error("Error Occured for busName:" + busName + ", locnNbr:" + locnNbr + " : " + e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting event counters for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}

	@GetMapping("/{busName}/{locnNbr}/orders/backlog")
	public ResponseEntity getOrderBacklog(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr) throws Exception {
		try {
			log.info("Received request for getOrderBacklog:" + busName + ":" + locnNbr);
			CounterStat pickHeatMap = orderCounterService.getOrderCounts(busName, locnNbr);
			log.info("Completed request for getOrderBacklog:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured for getOrderBacklog, busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting getOrderBacklog for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}

	@GetMapping("/{busName}/{locnNbr}/orders/stats/{numOfDays}")
	public ResponseEntity getHourlyOrderStatistics(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("numOfDays") Integer numOfDays) throws Exception {
		try {
			log.info("Received request for getHourlyOrderStatistics:" + busName + ":" + locnNbr);
			Map<String, Map<String, Long>> pickHeatMap = orderCounterService.getOrderHourlyStats(busName, locnNbr,
					numOfDays);
			log.info("Completed request for getHourlyOrderStatistics:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured for getHourlyOrderStatistics, busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting getHourlyOrderStatistics for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}

	@GetMapping("/{busName}/{locnNbr}/orders/picking/stats/{numOfDays}/{numOfTopPerformers}")
	public ResponseEntity getPickingStats(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("numOfDays") Integer numOfDays,
			@PathVariable("numOfTopPerformers") Integer numOfTopPerformers) throws Exception {
		try {
			log.info("Received request for getPickingStats:" + busName + ":" + locnNbr);
			Map<String, Map<String, Object>> pickHeatMap = orderCounterService.getPickingStats(busName, locnNbr, numOfDays, numOfTopPerformers);
			log.info("Completed request for getPickingStats:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured forgetPickingStats busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting getPickingStats for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}

}
