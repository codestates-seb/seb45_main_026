package com.server.domain.member.entity;

import com.server.global.entity.BaseEnum;

public enum Authority implements BaseEnum {

	ROLE_USER("사용자"),
	ROLE_ADMIN("관리자");

	private final String description;

	Authority(String description) {
		this.description = description;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getDescription() {
		return this.description;
	}
}
