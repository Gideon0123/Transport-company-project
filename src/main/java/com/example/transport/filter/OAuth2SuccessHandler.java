package com.example.transport.filter;

import com.example.transport.enums.UserStatus;
import com.example.transport.model.RefreshToken;
import com.example.transport.model.User;
import com.example.transport.repository.UserRepository;
import com.example.transport.service.JwtService;
import com.example.transport.service.RefreshTokenService;
import com.example.transport.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(name);
                    newUser.setProvider("GOOGLE");
                    newUser.setStatus(UserStatus.ACTIVE);
                    return userRepository.save(newUser);
                });

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        CookieUtil.addAccessToken(response, accessToken);
        CookieUtil.addRefreshToken(response, refreshToken.getToken());

        getRedirectStrategy().sendRedirect(request, response,
                "http://localhost:3000/oauth-success");
    }
}