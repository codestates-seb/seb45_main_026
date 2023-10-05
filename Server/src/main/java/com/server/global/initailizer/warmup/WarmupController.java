package com.server.global.initailizer.warmup;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warmup")
public class WarmupController {

    private final WarmupState warmupState;

    public WarmupController(WarmupState warmupState) {
        this.warmupState = warmupState;
    }

    @GetMapping
    public ResponseEntity<String> warmup() {

        if(warmupState.isWarmupCompleted()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("warming up...");
    }
}
