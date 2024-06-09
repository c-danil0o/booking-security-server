package com.komsije.booking.security;


import io.ous.jtoml.impl.Token;
import jakarta.mail.Header;
import jakarta.mail.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.metadata.core.ConstraintHelper.GROUPS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {
    @Autowired
    private static final String GROUPS = "groups";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    @Bean
    public FilterRegistrationBean<XSSFilter> filterRegistrationBean() {
        FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XSSFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/api/register*").permitAll()
                .requestMatchers("/api/register/**").permitAll()
                .requestMatchers("/api/logout").permitAll()
                .requestMatchers("/api/accommodations/search").permitAll()
                .requestMatchers("/api/accommodations/get/*").permitAll()
                .requestMatchers("/api/reviews/acc*").permitAll()
                .requestMatchers("/api/reviews/host*").permitAll()
                .requestMatchers("/upload/**").permitAll()
                .requestMatchers("/files/**").permitAll()
                .requestMatchers("/error/**").permitAll()
                .requestMatchers("/socket/**").permitAll()
                .requestMatchers("/socket/*").permitAll()
                .requestMatchers("/api/certificate/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico",
                        "/**.html", "/**.css", "/**.js", "/**.png", "/**.jpg", "/**.jpeg", "/images/**").anonymous()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated());

//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
        http.oauth2ResourceServer((oauth2) -> oauth2.jwt(token -> token.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())));
//        http.oauth2Login(Customizer.withDefaults()).logout((logout) -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/3"));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        http.headers(header -> header.contentSecurityPolicy(cs -> cs.policyDirectives("script-src 'self'; default-src 'self'")));
        return http.build();
    }
//    @Bean
//    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers(new AntPathRequestMatcher("/customers*", HttpMethod.OPTIONS.name()))
//                .permitAll()
//                .requestMatchers(new AntPathRequestMatcher("/customers*"))
//                .hasRole("user")
//                .requestMatchers(new AntPathRequestMatcher("/"))
//                .permitAll()
//                .anyRequest()
//                .authenticated());
//        http.oauth2ResourceServer((oauth2) -> oauth2
//                .jwt(Customizer.withDefaults()));
//        http.oauth2Login(Customizer.withDefaults())
//                .logout(logout -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/"));
//        return http.build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Autowired
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.ldapAuthentication().userDnPatterns("uid={0},ou=people").groupSearchBase("ou=groups").contextSource().url("ldap://localhost:8389/dc=springframework,dc=org")
//                .and()
//                .passwordCompare()
//                .passwordEncoder(new BCryptPasswordEncoder())
//                .passwordAttribute("userPassword");
//
//    }

//    @Bean
//    public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
//        return authorities -> {
//            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//            var authority = authorities.iterator().next();
//            boolean isOidc = authority instanceof OidcUserAuthority;
//
//            if (isOidc) {
//                var oidcUserAuthority = (OidcUserAuthority) authority;
//                var userInfo = oidcUserAuthority.getUserInfo();
//
//                // Tokens can be configured to return roles under
//                // Groups or REALM ACCESS hence have to check both
//                if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) {
//                    var realmAccess = userInfo.getClaimAsMap(REALM_ACCESS_CLAIM);
//                    var roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
//                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
//                } else if (userInfo.hasClaim(GROUPS)) {
//                    Collection<String> roles = (Collection<String>) userInfo.getClaim(GROUPS);
//                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
//                }
//            } else {
//                var oauth2UserAuthority = (OAuth2UserAuthority) authority;
//                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
//
//                if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) {
//                    Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get(REALM_ACCESS_CLAIM);
//                    Collection<String> roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
//                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
//                }
//            }
//            return mappedAuthorities;
//        };
//    }
//
//    Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(
//                Collectors.toList());
//    }
}