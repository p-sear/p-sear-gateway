package com.pser.gateway.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HomeApi {
    @GetMapping("/home")
    public Mono<ResponseEntity<String>> home() {
        return Mono.just(ResponseEntity.ok("It's home"));
    }
}
