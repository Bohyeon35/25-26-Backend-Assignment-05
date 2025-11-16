package com.example.oauthgoogleloginexample.controller;

import com.example.oauthgoogleloginexample.dto.TokenDto;
import com.example.oauthgoogleloginexample.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/oauth2")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/callback/google")
    public TokenDto googleCallback(@RequestParam("code") String code) {

        String googleAccessToken = authService.getGoogleAccessToken(code); // google 토큰 받기

        return authService.loginOrSignUp(googleAccessToken); // TokenDto로 변환
    }

}

