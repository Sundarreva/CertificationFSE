package com.cognizant.uploadexcelservice.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        LOGGER.info("START JWT FILTER");
        LOGGER.debug("{}: ", authenticationManager);
        LOGGER.info("END JWT FILTER");
    }
	@Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
        FilterChain chain) throws IOException, ServletException {
        LOGGER.info("START DO FILTER INTERNAL");
        
        String header = req.getHeader("Authorization");
        LOGGER.debug(header);
        
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
        
        LOGGER.info("END DO FILTER INTERNAL");
    }
	 private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		 	LOGGER.info("START AUTHENTICATE TOKEN");
	        String token = request.getHeader("Authorization");
	        if (token != null) {
	            Jws<Claims> jws;
	            try {
	                jws = Jwts.parser()
	                       .setSigningKey("secretkey")
	                       .parseClaimsJws(token.replace("Bearer ", ""));
	                
	                String user = jws.getBody().getSubject();
	                
	                LOGGER.debug(user);
	                
	                if (user != null) {
	                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
	                }
	            } catch (JwtException ex) {
	                return null;
	            }
	            return null;
	        }
	        LOGGER.info("END AUTHENTICATE TOKEN");
	        return null;
	    }
}