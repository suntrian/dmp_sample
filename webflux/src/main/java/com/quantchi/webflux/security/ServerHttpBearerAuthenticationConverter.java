package com.quantchi.webflux.security;

import com.quantchi.webflux.security.jwt.AuthorizationHeaderPayload;
import com.quantchi.webflux.security.jwt.JWTCustomVerifier;
import com.quantchi.webflux.security.jwt.UsernamePasswordAuthenticationBearer;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

public class ServerHttpBearerAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

  private static final String BEARER = "Bearer ";
  private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
  private static final Function<String,Mono<String>> isolateBearerValue = authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length()));

  private  JWTCustomVerifier jwtVerifier = new JWTCustomVerifier();
  /**
   * Apply this function to the current WebExchange, an Authentication object
   * is returned when completed.
   *
   * @param serverWebExchange
   * @return
   */
  @Override
  public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
    return Mono.justOrEmpty(serverWebExchange)
            .flatMap(AuthorizationHeaderPayload::extract)
            .filter(matchBearerLength)
            .flatMap(isolateBearerValue)
            .flatMap(jwtVerifier::check)
            .flatMap(UsernamePasswordAuthenticationBearer::create).log();
  }
}
