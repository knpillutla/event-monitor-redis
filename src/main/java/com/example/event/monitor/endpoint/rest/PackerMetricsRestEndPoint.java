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
public class PackerMetricsRestEndPoint {
	@Autowired
	EventMonitorService eventMonitorService;

	@Autowired
	OrderEventCounterService orderCounterService;

	@Value("${wms.service.health.msg: Event Monitor Service - Config Server is not working..please check}")
	private String healthMsg;

	@Value("${wms.service.ready.msg: Event Monitor Service - Not ready yet}")
	private String readyMsg;

	@GetMapping("/{busName}/{locnNbr}/packs/packer/stats/{numOfDays}/{numOfTopPerformers}")
	public ResponseEntity getTopPackersByHour(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr, @PathVariable("numOfDays") Integer numOfDays,
			@PathVariable("numOfTopPerformers") Integer numOfTopPerformers) throws Exception {
		try {
			log.info("Received request for getTopPackersByHour:" + busName + ":" + locnNbr);
			Map<String, Map<String, Long>> pickHeatMap = orderCounterService
					.getTopPackPerformersByHour(busName, locnNbr, numOfDays, numOfTopPerformers);
			log.info("Completed request for getTopPackersByHour:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured for getTopPackersByHour, busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting getTopPackersForCurrentHour for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}

	@GetMapping("/{busName}/{locnNbr}/packs/packer/stats")
	public ResponseEntity getTopPackersForCurrentHour(@PathVariable("busName") String busName,
			@PathVariable("locnNbr") Integer locnNbr) throws Exception {
		try {
			log.info("Received request for getTopPackersForCurrentHour:" + busName + ":" + locnNbr);
			Map<String, Map<String, Long>> pickHeatMap = orderCounterService.getTopPackPerformersForCurrentHour(busName,
					locnNbr);
			log.info("Completed request for getTopPackersForCurrentHour:" + busName + ":" + locnNbr + ":" + pickHeatMap);
			return ResponseEntity.ok(pickHeatMap);
		} catch (Exception e) {
			log.error("Error Occured for getTopPackersForCurrentHour, busName:" + busName + ", locnNbr:" + locnNbr + " : "
					+ e.getMessage());
			return ResponseEntity.badRequest()
					.body(new ErrorResourceDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"Error Occured while getting getTopPackersForCurrentHour for busName:" + busName + ", locnNbr:" + locnNbr
									+ " : " + e.getMessage()));
		}
	}
}
