package com.server.domain.reward.entity;

import com.server.global.entity.BaseEnum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RewardType implements BaseEnum {
	VIDEO("강의 결제로 얻은 리워드입니다."),
	QUIZ("문제 풀이로 얻은 리워드입니다.");

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
