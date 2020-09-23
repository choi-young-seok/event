package io.api.event.config.auth;

import io.api.event.common.BaseControllerTest;
import io.api.event.domain.entity.account.Account;
import io.api.event.domain.entity.account.AccountRole;
import io.api.event.repository.account.AccountRepository;
import io.api.event.service.account.AccountService;
import io.api.event.util.common.TestDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static io.api.event.config.auth.docs.AuthDocumentGenerator.getAuthDocument;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @TestDescription("Account 생성 및 인증 후 인증 토큰 발급 테스트")
    @DisplayName("AuthService : Account 생성 및 인증 토큰 발급")
    public void getAuthToken_Test() throws Exception {
        String userEmail = "test@naver.com";
        String userPassword = "test_password";

        Account userAccount = Account.builder()
                .email(userEmail)
                .password(userPassword)
                .roles(Set.of(AccountRole.ADMIN))
                .build();

        this.accountService.saveAccount(userAccount);

        String clientId = "myApp";
        String clientSecret = "pass";

        // When
        String urlTemplate = "/oauth/token";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .with(httpBasic(clientId, clientSecret)) // clientId와 clientSecret를 이용한 basicOath Header 생성
                .param("username", userEmail)
                .param("password", userPassword)
                .param("grant_type", "password")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
        ;
    }

    @Test
    @TestDescription("존재하는 Account의 인증 및 인증 토큰 발급 테스트")
    @DisplayName("AuthService : Account 인증 및 토큰 발급")
    public void getAuth() throws Exception {
        String clientId = applicationProperties.getCliendId();
        String clientSecret = applicationProperties.getClinetSecret();
        String userEmail = applicationProperties.getUserUserName();
        String userPassword = applicationProperties.getUserPassword();
        String grantType = applicationProperties.getGrantType();

        // When
        String urlTemplate = "/oauth/token";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .with(httpBasic(clientId, clientSecret)) // clientId와 clientSecret를 이용한 basicOath Header 생성
                .param("username", userEmail)
                .param("password", userPassword)
                .param("grant_type", grantType)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                .andDo(getAuthDocument())
        ;
    }
}