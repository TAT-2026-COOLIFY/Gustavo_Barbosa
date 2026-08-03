package com.ironfit.api.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ironfit.security.login-rate-limit")
public class LoginRateLimitProperties {

    private int capacity;
    private long refillMinutes;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public long getRefillMinutes() {
        return refillMinutes;
    }

    public void setRefillMinutes(long refillMinutes) {
        this.refillMinutes = refillMinutes;
    }
}
