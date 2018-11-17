package com.example.event.monitor.endpoint.rest;

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
public class PickerMetricsRestEndPoint {
	@Autowired
	EventMonitorService eventMonitorService;

	@Autowired
	OrderEventCounterService orderCounterService;

	@Autowired
	PickEventCounterService pickCounterService;

	@Value("${wms.service.health.msg: Event Monitor Service - Config Server is not working..please check}")
	private String healthMsg;

	@Value("${wms.service.ready.msg: Event Monitor Service - Not ready yet}")
	private String readyMsg;

	@GetMapping("/{busName}/{locnNbr}/picks/counter")
	public ResponseEntity getPickCounter(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr) throws Exception {
		try {
			log.info("Received request for pick counter:" + busName + ":" + locnNbr);
			CounterStat pickHeatMap = pickCounterService.getPickCounts(busName, locnNbr);
			log.info("Completed request for pick counter:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured for pick counter busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting pick counter for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}

	@GetMapping("/{busName}/{locnNbr}/picks/picker/stats/{numOfDays}/{numOfTopPerformers}")
	public ResponseEntity getTopPickersForPast24Hours(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("numOfDays") Integer numOfDays,
			@PathVariable("numOfTopPerformers") Integer numOfTopPerformers) throws Exception {
		try {
			log.info("Received request for order counter:" + busName + ":" + locnNbr);
			Map<String, Map<String, Long>> pickHeatMap = orderCounterService
					.getTopPickPerformersByHourForPast24Hours(busName, locnNbr, numOfDays, numOfTopPerformers);
			log.info("Completed request for order counter:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured for order counter busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting order counter for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}

/*	@GetMapping("/{busName}/{locnNbr}/picks/picker/stats/{numOfDays}/{numOfTopPerformers}")
	public ResponseEntity getTopPickersForPast24Hours(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("numOfDays") Integer numOfDays,
			@PathVariable("numOfTopPerformers") Integer numOfTopPerformers) throws Exception {
		try {
			log.info("Received request for order counter:" + busName + ":" + locnNbr);
			Map<String, Map<String, Long>> pickHeatMap = orderCounterService
					.getTopPickPerformersByHourForPast24Hours(busName, locnNbr, numOfDays, numOfTopPerformers);
			log.info("Completed request for order counter:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured for order counter busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting order counter for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}
*/
	@GetMapping("/{busName}/{locnNbr}/picks/picker/stats")
	public ResponseEntity getTopPickersForCurrentHour(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr) throws Exception {
		try {
			log.info("Received request for order counter:" + busName + ":" + locnNbr);
			Map<String, Map<String, Long>> pickHeatMap = orderCounterService.getTopPickPerformersForCurrentHour(busName,
					locnNbr);
			log.info("Completed request for order counter:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured for order counter busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting order counter for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}
}
