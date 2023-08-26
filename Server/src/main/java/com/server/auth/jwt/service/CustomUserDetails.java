package com.server.auth.jwt.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.server.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class CustomUserDetails extends User {

	private final Long memberId;

	public CustomUserDetails(Long memberId, String email, String password, Collection<? extends GrantedAuthority> authorities) {
		super(email, password, authorities);
		this.memberId = memberId;
	}
}
