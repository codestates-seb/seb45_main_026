package com.server.auth.service;

import java.util.Optional;
import java.util.Random;

import javax.mail.internet.MimeMessage;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.auth.service.dto.AuthServiceRequest;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.MemberService;
import com.server.global.exception.businessexception.mailexception.MailCertificationException;
import com.server.global.exception.businessexception.mailexception.MailSendException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.module.email.service.MailService;
import com.server.module.redis.service.RedisService;

@Service
@Transactional
public class AuthService {

	private final MailService mailService;
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisService redisService;

	public AuthService(MailService mailService, MemberService memberService, MemberRepository memberRepository,
		PasswordEncoder passwordEncoder, RedisService redisService) {
		this.mailService = mailService;
		this.memberService = memberService;
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.redisService = redisService;
	}

	public void sendEmail(AuthServiceRequest.Send request, String type) {
		String email = request.getEmail();
		String code = createCode();

		if(type.equals("signup")) {
			memberService.checkDuplicationEmail(email);
		}
		else {
			checkExistMember(email);
		}

		try{
			mailService.sendEmail(createMessage(email, code));
			redisService.setExpire(code, email, 300);
		}
		catch (Exception m) {
			m.printStackTrace();
			throw new MailSendException();
		}
	}

	public void updatePassword(AuthServiceRequest.Reset request) {
		checkEmailCertify(request.getEmail());

		Member member = checkExistMember(request.getEmail());
		member.updatePassword(passwordEncoder.encode(request.getPassword()));
	}

	private Member checkExistMember(String email) {
		return memberRepository.findByEmail(email).orElseThrow(
			MemberNotFoundException::new
		);
	}

	private MimeMessage createMessage(String email, String code) throws Exception {
		String subject = "프로메테우스 사용자 인증을 위한 인증번호입니다.";
		String text = "<h1>프로메테우스 인증번호</h1>" +
			"<p>인증번호는 " + code + " 입니다.</p>";

		return mailService.createMessage(email, subject, text);
	}

	private String createCode() {
		Random random = new Random();
		StringBuffer key = new StringBuffer();

		for (int i = 0; i < 8; i++) {
			int idx = random.nextInt(3);

			switch (idx) {
				case 0 :
					key.append((char) ((int)random.nextInt(26) + 97));
					break;
				case 1:
					key.append((char) ((int)random.nextInt(26) + 65));
					break;
				case 2:
					key.append(random.nextInt(9));
					break;
			}
		}

		return key.toString();
	}

	public void verifyEmail(AuthServiceRequest.Confirm request) throws MailCertificationException {
		String email = request.getEmail();

		String getEmail = redisService.getData(request.getCode());
		Optional<String> optional = Optional.ofNullable(getEmail);

		if (getEmail == null || (optional.isPresent() && !email.equals(optional.get()))) {
			throw new MailCertificationException();
		}

		redisService.setExpire(email, "true", 300);
	}

	private void checkEmailCertify(String email) {
		if (!"true".equals(redisService.getData(email))) {
			throw new MailCertificationException();
		}
		redisService.deleteData(email);
	}
}
