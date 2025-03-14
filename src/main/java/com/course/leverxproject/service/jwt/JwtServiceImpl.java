package com.course.leverxproject.service.jwt;

import com.course.leverxproject.service.auth.MyUserDetails;
import com.course.leverxproject.service.auth.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private final MyUserDetailsService myUserDetailsService;
    private String secretKey = "";

    public JwtServiceImpl(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String generateToken(String email) {

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 600000))
                .and()
                .signWith(getKey())
                .compact();
    }

    @Override
    public String generateToken(String email, String userType) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userType);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 600000))
                .and()
                .signWith(getKey())
                .compact();
    }

    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractUserType(String token) {
        return extractAllClaims(token).get("userType", String.class);
    }


    @Override
    public boolean validateToken(String token, UserDetails userDetails) {

        final String subject = extractSubject(token);
        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } else {
            int userId = ((MyUserDetails) userDetails).getId();
            return subject.equals(String.valueOf(userId)) && !isTokenExpired(token);
        }
    }

    @Override
    public Authentication getAuthentication(String token) {
        String id = extractSubject(token);
        UserDetails userDetails = myUserDetailsService.loadByUserId(id);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}