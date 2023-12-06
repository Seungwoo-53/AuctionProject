package com.auction.WebAuction.config;

import com.auction.WebAuction.model.Member;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.sql.DataSource;

import java.io.IOException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private DataSource dataSource;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        try {
            http
                    .authorizeHttpRequests(request -> request
                            .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                            .requestMatchers("/images/**", "/", "/css/**", "/member/signup", "/member/login").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(login -> login
                            .loginPage("/member/login")    // [A] 커스텀 로그인 페이지 지정
                            .loginProcessingUrl("/member/login")    // [B] submit 받을 url
                            .usernameParameter("username")    // [C] submit할 아이디
                            .passwordParameter("userpass")    // [D] submit할 비밀번호
                            .defaultSuccessUrl("/", true)

                            .permitAll()
                    )
                    .logout(withDefaults());
        } catch (Exception e){
            throw new RuntimeException(e);
        }


        return http.build();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, userpass, enabled "
                        + "from member "
                        + "where username = ? ")
                .authoritiesByUsernameQuery("select m.username, r.name "
                        + "from member_role mr inner join member m on mr.member_id = m.id "
                        + "inner join role r on mr.role_id = r.id "
                        + "where m.username = ? ");
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}