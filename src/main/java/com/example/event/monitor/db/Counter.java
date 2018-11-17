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
public class Counter {
	String type;
	String name; //area/zone/aisle
	String altName;
	String value;
	Long count;
 	List<Counter> counterList = new ArrayList();
	
	public void addCounter(Counter counterDetail) {
		counterList.add(counterDetail);
	}
	
}
