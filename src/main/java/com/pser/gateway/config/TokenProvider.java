package com.pser.gateway.config;

import com.pser.gateway.Util;
import com.pser.gateway.dao.UserDao;
import com.pser.gateway.domain.User;
import com.pser.gateway.dto.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TokenProvider {
    private final Key key;
    private final Long tokenValidityInSeconds;
    private final UserDao userDao;

    public TokenProvider(Environment env, UserDao userDao) {
        String secret = env.getProperty("jwt.secret");
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.tokenValidityInSeconds = Util.getLongProperty(env, "jwt.token-validity-in-seconds");
        this.userDao = userDao;
    }

    public Mono<Authentication> getAuthentication(String token) {
        Claims claims = getClaims(token);
        String email = claims.getSubject();
        return userDao.findByEmail(email)
                .map(this::toAuthenticationToken);
    }

    private Date getExpiration() {
        long nowMilliSeconds = new Date().getTime();
        long tokenValidityInMilliSeconds = tokenValidityInSeconds * 1000;
        long expirationMilliSeconds = nowMilliSeconds + tokenValidityInMilliSeconds;
        return new Date(expirationMilliSeconds);
    }

    private Claims getClaims(String token) {
        Claims claims;
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();
            claims = jwtParser
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException | MalformedJwtException e) {
            throw new MalformedJwtException("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            throw new MalformedJwtException("JWT 토큰이 잘못되었습니다");
        }
        return claims;
    }

    private UsernamePasswordAuthenticationToken toAuthenticationToken(User user) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        UserDetails principle = new UserDetailsImpl(
                user.getId(), user.getEmail(), user.getPassword(), authorities
        );
        return new UsernamePasswordAuthenticationToken(principle, user.getPassword(), principle.getAuthorities());
    }
}
