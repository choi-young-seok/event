package io.api.event.common.auth;

import io.api.event.config.ApplicationProperties;
import io.api.event.domain.entity.account.Account;
import io.api.event.domain.entity.account.AccountRole;
import io.api.event.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
public class AuthInfoGenerator {

    @Autowired
    AccountService accountService;

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    MockMvc mockMvc;

    public String getBearerToken() throws Exception {
        return "Bearer " + this.getAccessToken();
    }

    public String getAccessToken() throws Exception {
        // Given
        String clientId = applicationProperties.getCliendId();
        String clientSecret = applicationProperties.getClinetSecret();
        String userEmail = applicationProperties.getUserUserName();
        String userPassword = applicationProperties.getUserPassword();
        String grantType = applicationProperties.getGrantType();

        Account userAccount = Account.builder()
                .email(userEmail)
                .password(userPassword)
                .roles(Set.of(AccountRole.USER))
                .build();
        this.accountService.saveAccount(userAccount);

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

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jackson2JsonParser = new Jackson2JsonParser();
        return jackson2JsonParser.parseMap(responseBody).get("access_token").toString();
    }
}
