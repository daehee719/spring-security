package com.example.springsecurity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    // http 주입 받아서
    // 어떤 요청이든지 인증을 받도록 하고,
    // 인증이 안되었다면 다시 인증을 받기 위해 formLogin으로 이동

    // 이 빈을 만듬으로써
    // SpringBootWebSecurityConfiguration의 defaultSercurityFilterChain이 실행되지 않는다.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(auth -> auth.anyRequest().authenticated())
                // .formLogin(Customizer.withDefaults()); 기본 로그인 폼
                .formLogin(form -> form
//                        .loginPage("/loginPage")
                        .loginProcessingUrl("/loginProc")
                        // false로 하면 기존 페이지로 리다이렉션 됨
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/fail")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        // 인증되면 /home 이라는 url로 리다이렉션
                        // 재정의를 하면 위의 default 메서드들을 무시됨
                        .successHandler((request, response, authentication) ->
                        {
                            System.out.println("authentication : " + authentication);
                            response.sendRedirect("/home");
                        })
                        // 이렇게도 가능
                        .failureHandler((request, response, exception) -> {
                            System.out.println("exception : " + exception.getMessage());
                            response.sendRedirect("/login");
                        })
                        // 인증이 되어있지 않더라도 이동 가능
                        .permitAll()
                );


        return http.build();
    }

    // 인메모리 방식
    // 사용자를 빈으로 추가
    @Bean
    public UserDetailsManager inMemoryUserDetailsManager() {
        UserDetails user1 = User.withUsername("user1")
                .password("{noop}1111")
                .authorities("USER")
                .build();

        UserDetails user2 = User.withUsername("user2")
                .password("{noop}2222")
                .authorities("USER")
                .build();
        return new InMemoryUserDetailsManager(user1, user2);
    }
}
