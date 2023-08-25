package com.server.auth;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import com.server.auth.controller.AuthController;

@WebMvcTest(AuthController.class)
// @Import(AopConfiguration.class)
// @EnableAspectJAutoProxy
public class AuthControllerTest {
}
