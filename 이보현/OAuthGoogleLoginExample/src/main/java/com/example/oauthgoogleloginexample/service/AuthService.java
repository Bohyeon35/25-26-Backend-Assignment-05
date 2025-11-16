package com.example.oauthgoogleloginexample.service;

import com.example.oauthgoogleloginexample.domain.Role;
import com.example.oauthgoogleloginexample.domain.User;
import com.example.oauthgoogleloginexample.dto.TokenDto;
import com.example.oauthgoogleloginexample.dto.UserInfoDto;
import com.example.oauthgoogleloginexample.jwt.TokenProvider;
import com.example.oauthgoogleloginexample.repository.UserRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${google.token-url}")
    private String GOOGLE_TOKEN_URL;

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public String getGoogleAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = Map.of(
                "code", code,
                "scope", "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email",
                "client_id", GOOGLE_CLIENT_ID,
                "client_secret", GOOGLE_CLIENT_SECRET,
                "redirect_uri", GOOGLE_REDIRECT_URI,
                "grant_type", "authorization_code"
        );

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();

            return gson.fromJson(json, TokenDto.class).getAccessToken();
        }

        throw new RuntimeException("구글 엑세스 토큰을 가져오는데 실패했습니다.");
    }

    public TokenDto loginOrSignUp(String googleAccessToken) {
        UserInfoDto userInfoDto = getUserInfo(googleAccessToken);

        if (!userInfoDto.getVerifiedEmail()) {
            throw new RuntimeException("이메일 인증이 되지 않은 유저입니다.");
        }

        User user = userRepository.findByEmail(userInfoDto.getEmail()).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(userInfoDto.getEmail())
                        .name(userInfoDto.getName())
                        .pictureUrl(userInfoDto.getPictureUrl())
                        .role(Role.ROLE_USER)
                        .build())
        );

        return tokenProvider.createToken(user);
    }

    public UserInfoDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String json = responseEntity.getBody();
            Gson gson = new Gson();
            return gson.fromJson(json, UserInfoDto.class);
        }

        throw new RuntimeException("유저 정보를 가져오는데 실패했습니다.");
    }

    public User test(Principal principal) {
        Long id = Long.parseLong(principal.getName());

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }
}
