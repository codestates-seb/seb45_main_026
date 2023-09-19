package com.server.global.p6spy.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.p6spy.engine.spy.P6SpyOptions;
import org.springframework.context.annotation.Profile;

@Configuration
public class P6spyConfig {
	@PostConstruct
	public void setLogMessageFormat() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(P6spySqlFormatConfiguration.class.getName());
	}
}
