package com.example.event.monitor.service;

import com.threedsoft.util.dto.events.WMSEvent;

public interface MessagePublisher {
    void publish(WMSEvent wmsEvent);
}
