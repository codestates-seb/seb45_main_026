package com.server.global.initailizer.warmup;

import org.springframework.stereotype.Component;

@Component
public class WarmupState {

    private boolean isWarmupCompleted = false;

    public boolean isWarmupCompleted() {
        return isWarmupCompleted;
    }

    public void setWarmupCompleted(boolean isWarmupCompleted) {
        this.isWarmupCompleted = isWarmupCompleted;
    }
}
