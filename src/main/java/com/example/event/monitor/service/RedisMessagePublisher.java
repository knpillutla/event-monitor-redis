package com.example.event.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import com.threedsoft.util.dto.events.WMSEvent;

public class RedisMessagePublisher implements MessagePublisher {
	 
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic topic;
 
    public RedisMessagePublisher() {
    }
 
    public RedisMessagePublisher(
      RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
      this.redisTemplate = redisTemplate;
      this.topic = topic;
    }
 
    public void publish(WMSEvent wmsEvent) {
        redisTemplate.setEnableDefaultSerializer(true);
        redisTemplate.convertAndSend(topic.getTopic(), wmsEvent);
    }
}