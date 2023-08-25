package com.server.module.email.service;

import java.util.Optional;
import java.util.Random;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.server.global.exception.businessexception.mailexception.MailCertificationException;
import com.server.global.exception.businessexception.mailexception.MailSendException;
import com.server.module.redis.service.RedisService;

@Service
public class MailService {
	private final JavaMailSender javaMailSender;
	private final RedisService redisService;
	private final String randomNumber = createCode();

	public MailService(JavaMailSender javaMailSender, RedisService redisService) {
		this.javaMailSender = javaMailSender;
		this.redisService = redisService;
	}

	private MimeMessage createMessage(String email) throws Exception {
		MimeMessage message = javaMailSender.createMimeMessage();

		message.addRecipients(Message.RecipientType.TO, email);
		message.setSubject("프로메테우스 회원가입을 위한 인증번호입니다.");
		message.setText(
			"<h1>프로메테우스 회원가입 인증번호</h1>" +
			"<p>인증번호는 " + randomNumber + " 입니다.</p>"
			,"utf-8", "html"
		);
		message.setFrom(new InternetAddress("yj171151@naver.com", "prometheus"));

		return message;
	}

	public String sendEmail(String email) throws Exception {
		try {
			MimeMessage mimeMessage = createMessage(email);
			redisService.setExpire(randomNumber, email, 300);
			javaMailSender.send(mimeMessage);
		} catch (MailException me) {
			me.printStackTrace();
			throw new MailSendException();
		}

		return randomNumber;
	}

	public static String createCode() {
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

	public void verifyEmail(String email, String authCode) throws MailCertificationException {
		String getEmail = redisService.getData(authCode);
		Optional<String> optional = Optional.ofNullable(getEmail);

		if (getEmail == null || (optional.isPresent() && !email.equals(optional.get()))) {
			throw new MailCertificationException();
		}

		redisService.setExpire(email, "true", 300);
	}

	public void checkEmailCertify(String email) {
		if (!"true".equals(redisService.getData(email))) {
			throw new MailCertificationException();
		}
		redisService.deleteData(email);
	}
}
