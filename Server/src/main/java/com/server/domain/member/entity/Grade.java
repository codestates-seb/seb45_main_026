package com.server.domain.member.entity;

import org.aspectj.lang.annotation.Aspect;

import com.server.global.entity.BaseEnum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Grade implements BaseEnum {
	BRONZE("브론즈"),
	SILVER("실버"),
	GOLD("골드"),
	PLATINUM("플래티넘"),
	DIAMOND("다이아몬드");

	private final String description;

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDescription() {
		return this.description;
	}
}
