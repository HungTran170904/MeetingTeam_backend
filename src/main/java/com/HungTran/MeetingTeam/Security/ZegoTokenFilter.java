package com.HungTran.MeetingTeam.Security;

import com.HungTran.MeetingTeam.Exception.PermissionException;
import com.HungTran.MeetingTeam.Exception.RequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class ZegoTokenFilter extends OncePerRequestFilter {
    @Value("${zegocloud.secret-server}")
    private String zegoSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("ZegoTokenFilter");
        //  Obtain the value of signature, timestamp, and nonce from the request parameters.
        String signature = request.getParameter("signature");
        long timestamp = Long.parseLong(request.getParameter("timestamp"));
        String nonce = request.getParameter("nonce");

        // Use the CallbackSecret obtained from the ZEGO Admin Console.
        String secret =  zegoSecret;

        String[] tempArr = {secret, ""+timestamp, nonce};
        Arrays.sort(tempArr);

        String tmpStr = "";
        for (int i = 0; i < tempArr.length; i++) {
            tmpStr += tempArr[i];
        }
        tmpStr = org.apache.commons.codec.digest.DigestUtils.sha1Hex(tmpStr);

        if(tmpStr.equals(signature)) chain.doFilter(request, response);
        else throw new RequestException("Signature does not match");
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getRequestURI().startsWith("/api/zegocloud");
    }
}
