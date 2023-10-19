package com.server.module.email.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

	@Value("${spring.mail.username}")
	private String id;
	@Value("${spring.mail.password}")
	private String password;
	@Value("${spring.mail.host}")
	private String host;
	@Value("${spring.mail.port}")
	private int port;
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private boolean auth;

	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private boolean starttlsEnable;

	@Value("${spring.mail.properties.mail.smtp.starttls.required}")
	private boolean starttlsRequired;

	@Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
	private int connectionTimeout;

	@Value("${spring.mail.properties.mail.smtp.timeout}")
	private int timeout;

	@Value("${spring.mail.properties.mail.smtp.writetimeout}")
	private int writeTimeout;

	@Bean
	public JavaMailSender javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		javaMailSender.setHost(host);
		javaMailSender.setUsername(id);
		javaMailSender.setPassword(password);
		javaMailSender.setPort(port);
		javaMailSender.setJavaMailProperties(getMailProperties());
		javaMailSender.setDefaultEncoding("UTF-8");

		return javaMailSender;
	}

	private Properties getMailProperties() {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", auth);
		properties.put("mail.smtp.starttls.enable", starttlsEnable);
		properties.put("mail.smtp.starttls.required", starttlsRequired);
		properties.put("mail.smtp.connectiontimeout", connectionTimeout);
		properties.put("mail.smtp.timeout", timeout);
		properties.put("mail.smtp.writetimeout", writeTimeout);

		return properties;
	}
}
