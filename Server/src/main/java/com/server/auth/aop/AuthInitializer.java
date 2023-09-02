package com.server.auth.aop;

import javax.annotation.PostConstruct;

import com.server.domain.category.entity.Category;
import com.server.domain.category.entity.CategoryRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;

import java.util.List;

@Component
@Profile("local")
public class AuthInitializer {
	private final MemberRepository memberRepository;
	private final ChannelRepository channelRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthInitializer(MemberRepository memberRepository, ChannelRepository channelRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.channelRepository = channelRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void initialize() {
		Member member = Member.createMember(
				"test@email.com",
				passwordEncoder.encode("qwer1234!"),
				"테스트 사용자"
		);

		Channel channel = Channel.createChannel("test-channel");

		memberRepository.save(member);
		channelRepository.save(channel);
	}
}
