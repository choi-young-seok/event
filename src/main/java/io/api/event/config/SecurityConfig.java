package io.api.event.config;

import io.api.event.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration //Bean 설정 파일
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

//    final AccountService accountService;
//
//    final PasswordEncoder passwordEncoder;
//
//    public SecurityConfig(AccountService accountService, PasswordEncoder passwordEncoder) {
//        this.accountService = accountService;
//        this.passwordEncoder = passwordEncoder;
//    }

    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    /**
     * security 필터 적용 유무 : 인증을 무시할 요청을 정의 한다.
     * */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // docs에 접근 하는 요청 인증 처리 제외
        web.ignoring().mvcMatchers("/docs/index.html");

        // Spring Boot가 제공하는 PathRequest를 이용하여 정적 자원(static)의 경로에 접근하는 요청 인증 제외 처리
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * 인증 과정 없이 허용할 http 요청과 인증 과정이 필요한 http 요청을 정의 한다.
     * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .anonymous()
                .and()
            .formLogin()
                .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**").anonymous()
            .anyRequest().authenticated();
    }
}



