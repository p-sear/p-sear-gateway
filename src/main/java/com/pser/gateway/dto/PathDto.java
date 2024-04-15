package com.pser.gateway.dto;

import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

@Data
public class PathDto {
    private String pattern;
    private String method;

    public PathPatternParserServerWebExchangeMatcher toMatcher() {
        if (method == null) {
            return new PathPatternParserServerWebExchangeMatcher(pattern);
        }
        return new PathPatternParserServerWebExchangeMatcher(pattern, HttpMethod.valueOf(method.toUpperCase()));
    }
}
