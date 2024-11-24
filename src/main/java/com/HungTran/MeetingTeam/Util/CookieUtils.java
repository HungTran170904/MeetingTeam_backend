package com.HungTran.MeetingTeam.Util;

import com.HungTran.MeetingTeam.Config.JwtConfig;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtils {
    private final JwtConfig jwtConfig;

    public Cookie generateTokenCookie(String token){
        Cookie cookie=new Cookie(jwtConfig.header, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(jwtConfig.expiration/1000);
        if(jwtConfig.cookieDomain!=null&&!jwtConfig.cookieDomain.isEmpty()){
            cookie.setDomain(jwtConfig.cookieDomain);
        }
        cookie.setAttribute("sameSite","Strict");
        return cookie;
    }

    public Cookie generateExpiredCookie(){
        Cookie cookie = new Cookie(jwtConfig.header, null);
        if(jwtConfig.cookieDomain!=null&&!jwtConfig.cookieDomain.isEmpty()){
            cookie.setDomain(jwtConfig.cookieDomain);
        }
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
