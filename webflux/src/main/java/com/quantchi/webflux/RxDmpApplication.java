package com.quantchi.webflux;

import com.quantchi.webflux.security.BasicAuthenticationSuccessHandler;
import com.quantchi.webflux.security.BearerTokenReactiveAuthenticationManager;
import com.quantchi.webflux.security.MessageService;
import com.quantchi.webflux.security.ServerHttpBearerAuthenticationConverter;
import org.apache.logging.log4j.message.FormattedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RestController
@SpringBootApplication
@EnableWebFluxSecurity
public class RxDmpApplication {

  public static void main(String[] args) {
    SpringApplication.run(RxDmpApplication.class, args);
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsRepository(){
    UserDetails user = User.withDefaultPasswordEncoder()
            .username("react")
            .password("reactive")
            .roles("ADMIN", "USER")
            .build();
    return new MapReactiveUserDetailsService(user);
  }

  @Bean
  public SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity httpSecurity){
    return httpSecurity
            .authorizeExchange()
            .pathMatchers("/login","/")
            .authenticated()
            .and()
            .addFilterAt(basicAuthenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
            .authorizeExchange()
            .pathMatchers("/api/**")
            .authenticated()
            .and()
            .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
  }

  private AuthenticationWebFilter basicAuthenticationFilter(){
    UserDetailsRepositoryReactiveAuthenticationManager authManager;
    AuthenticationWebFilter basicAuthenticationFilter;
    ServerAuthenticationSuccessHandler successHandler;

    authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsRepository());
    successHandler = new BasicAuthenticationSuccessHandler();

    basicAuthenticationFilter = new AuthenticationWebFilter(authManager);
    basicAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);

    return basicAuthenticationFilter;
  }

  private AuthenticationWebFilter bearerAuthenticationFilter(){
    AuthenticationWebFilter bearerAuthenticationFilter;
    Function<ServerWebExchange, Mono<Authentication>> bearerConverter;
    ReactiveAuthenticationManager authManager;

    authManager  = new BearerTokenReactiveAuthenticationManager();
    bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
    bearerConverter = new ServerHttpBearerAuthenticationConverter();

    bearerAuthenticationFilter.setAuthenticationConverter(bearerConverter);
    bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));

    return bearerAuthenticationFilter;
  }


  @Autowired
  MessageService messageService;

  /**
   * Root endpoint serves as a resource for Basic Authentication
   *
   * @return A publisher that serves a welcoming message
   */
  @GetMapping("/")
  public Flux<FormattedMessage> hello() {
    return messageService.getDefaultMessage();
  }

  /**
   * Common login endpoint is also available for basic authentication
   *
   * @return A publisher serving a message stating successful log in
   */
  @GetMapping("/login")
  public Flux<FormattedMessage> login() {
    return messageService.getDefaultMessage();
  }

  /**
   * A restricted endpoint requiring consumers to be authenticated and also
   * have the right roles for this resource
   *
   * @return A publisher serving a message when access is granted
   */
  @GetMapping("/api/private")
  @PreAuthorize("hasRole('USER')")
  public Flux<FormattedMessage> privateMessage() {
    return messageService.getCustomMessage("User");
  }

  /**
   * A restricted endpoint requiring consumers to be authenticated and also
   * have the right roles for this resource
   *
   * @return A publisher serving a message when access is granted
   */
  @GetMapping("/api/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public Flux<FormattedMessage> privateMessageAdmin() {
    return messageService.getCustomMessage("Admin");
  }

  /**
   * A restricted endpoint requiring consumers to be authenticated and also
   * have the right roles for this resource
   *
   * @return A publisher serving a message when access is granted
   */
  @GetMapping("/api/guest")
  @PreAuthorize("hasRole('GUEST')")
  public Flux<FormattedMessage> privateMessageGuest() {
    return messageService.getCustomMessage("Guest");
  }

}
