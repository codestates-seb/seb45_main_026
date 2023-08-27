package com.server.module.email.service.dto;

import lombok.Builder;
import lombok.Getter;

public class MailServiceRequest {
	@Getter
	@Builder
	public static class Confirm {
		private String email;

		private String code;
	}
}
