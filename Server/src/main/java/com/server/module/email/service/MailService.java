package com.server.module.email.service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	private final JavaMailSender javaMailSender;

	public MailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public MimeMessage createMessage(String email, String subject, String text) throws Exception {
		MimeMessage message = javaMailSender.createMimeMessage();

		message.addRecipients(Message.RecipientType.TO, email);
		message.setSubject(subject);
		message.setText(
			text,"utf-8", "html"
		);
		message.setFrom(new InternetAddress("sksjsksh32@google.com", "prometheus"));

		return message;
	}

	public void sendEmail(MimeMessage message) {
		javaMailSender.send(message);
	}
}
