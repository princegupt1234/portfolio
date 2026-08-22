package com.example.portfolio.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DataVersionService {
    private final AtomicLong version = new AtomicLong(System.currentTimeMillis());

    public void bump() {
        version.set(System.currentTimeMillis());
    }

    public long getVersion() {
        return version.get();
    }
}
