package com.server.global.initailizer.warmup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WarmupState {

    @Value("${warmup.is-completed}")
    private boolean isWarmupCompleted;

    public boolean isWarmupCompleted() {
        return isWarmupCompleted;
    }

    public void setWarmupCompleted(boolean isWarmupCompleted) {
        this.isWarmupCompleted = isWarmupCompleted;
    }
}
