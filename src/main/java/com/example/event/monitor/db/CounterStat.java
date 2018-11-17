package com.example.event.monitor.db;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@NoArgsConstructor
@Data
@AllArgsConstructor
public class CounterStat {
	String busName;
	Integer locnNbr;
	String counterType; // picks, orders, packs
	String counterDescription;
	String displayType; //heatmap, piechart
	public static final String HEATMAP_DISPLAY="HeatMap";
	public static final String PIECHART_DISPLAY="PieChart";
	public static final String LINE_CHART_DISPLAY="LineChart";
	List<Counter> counters = new ArrayList();
	
	public void addCounter(Counter counter) {
		counters.add(counter);
	}
}
