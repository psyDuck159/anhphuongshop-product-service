package biz.anhld.anhphuongshop.productservice.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Quan trọng: Để kích hoạt @PreAuthorize
public class SecurityConfig {

  interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter(AuthoritiesConverter authoritiesConverter) {
    var authenticationConverter = new JwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
      return authoritiesConverter.convert(jwt.getClaims());
    });
    return authenticationConverter;
  }

  @Bean
  AuthoritiesConverter realmRolesAuthoritiesConverter() {
    return claims -> {
      var realmAccess = Optional.ofNullable((Map<String, Object>) claims.get("realm_access"));
      var roles = realmAccess.flatMap(map -> Optional.ofNullable((List<String>) map.get("roles")));
      return roles.map(List::stream)
          .orElse(Stream.empty())
          .map(SimpleGrantedAuthority::new)
          .map(GrantedAuthority.class::cast)
          .toList();
    };
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      Converter<Jwt, AbstractAuthenticationToken> authenticationConverter) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter)));
    return http.build();
  }

  // @Bean
  // public JwtAuthenticationConverter jwtAuthenticationConverter() {
  // JwtGrantedAuthoritiesConverter authoritiesConverter = new
  // JwtGrantedAuthoritiesConverter();
  // // Không cần prefix "SCOPE_", chúng ta sẽ tự định nghĩa ROLE_
  // authoritiesConverter.setAuthorityPrefix("");

  // return new JwtAuthenticationConverter() {
  // @Override
  // protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
  // // Lấy các role từ realm_access.roles
  // Map<String, Object> realmAccess = jwt.getClaim("realm_access");
  // if (realmAccess == null || !realmAccess.containsKey("roles")) {
  // return List.of();
  // }

  // List<String> roles = (List<String>) realmAccess.get("roles");
  // return roles.stream()
  // .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName)) // Thêm tiền
  // tố ROLE_
  // .collect(Collectors.toList());
  // }
  // };
  // }
}
