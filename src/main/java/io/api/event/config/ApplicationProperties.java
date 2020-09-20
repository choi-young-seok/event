package io.api.event.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

/**
 * Application 구동 시 설정 파일 정보를 주입 하여 코드내 하드코딩 및 설정 부 제거
 */
@Component // Bean으로 등록
@ConfigurationProperties(prefix = "my-app")
@Getter
@Setter
public class ApplicationProperties {

    @NotEmpty
    private String adminUserName;
    @NotEmpty
    private String adminPassword;

    @NotEmpty
    private String userUserName;
    @NotEmpty
    private String userPassword;

    @NotEmpty
    private String cliendId;
    @NotEmpty
    private String clinetSecret;
    @NotEmpty
    private String grantType;
    @NotEmpty
    private String grantTypeValue;

}
