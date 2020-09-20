package io.api.event.config;

import io.api.event.common.BaseControllerTest;
import io.api.event.domain.entity.account.Account;
import io.api.event.domain.entity.account.AccountRole;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("")
    @DisplayName("AuthService : 인증 토큰 발급 테스트")
    public void getAuthToken() throws Exception {
        // Given
        String email = "rcn115@naver.com";
        String password = "chldydtjr1!";
        Set<AccountRole> roles = Set.of(AccountRole.ADMIN, AccountRole.USER);

        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(roles)
                .build();

        Account savedAccount = this.accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        // When
        String urlTemplate = "/oauth/token";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .with(httpBasic(clientId, clientSecret)) // clientId와 clientSecret를 이용한 basicOath Header 생성
                .param("username", email)
                .param("password", password)
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

}