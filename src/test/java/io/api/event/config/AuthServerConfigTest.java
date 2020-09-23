package io.api.event.config;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
    public void getAuthInfo() throws Exception {
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
//                .andDo(document("get-auth-info",
                .andDo(this.restDocumentationResultHandler.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type haeder"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("client ID와 clientSecret으로 이루어진 인증 정보")
                        ),
                        requestParameters(
                                parameterWithName("username").description("이메일"),
                                parameterWithName("password").description("패스워드"),
                                parameterWithName("grant_type").description("인증 타입")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Response content type")
                        ),
                        responseFields(
                                fieldWithPath("access_token").description("접근 토큰"),
                                fieldWithPath("token_type").description("토큰 타입"),
                                fieldWithPath("refresh_token").description("접근 토큰 갱신 토큰"),
                                fieldWithPath("expires_in").description("만료기간"),
                                fieldWithPath("scope").description("접근 권한 요청")
                        )
                ))
        ;
    }
}