package com.speechcoach.demo.Util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {
    public String extractTokenFromRequest(HttpServletRequest request, String cookieName){
        if(request.getCookies()==null) {
        return null;
        }
        for(Cookie cookie: request.getCookies()){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }

}
