package pairmate.gateway_service.jwt;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.net.HttpCookie;

@Component
public class CookieProvider {

    private static final String ACCESS_TOKEN_NAME = "ACCESS_TOKEN";

    public String getTokenFromCookies(MultiValueMap<String, HttpCookie> cookies) {
        HttpCookie cookie = cookies.getFirst(ACCESS_TOKEN_NAME);
        return cookie != null ? cookie.getValue() : null;
    }
}