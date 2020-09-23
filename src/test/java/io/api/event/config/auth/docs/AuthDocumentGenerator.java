package io.api.event.config.auth.docs;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

public class AuthDocumentGenerator {

    public static RestDocumentationResultHandler getAuthDocument() {
        return document("{class-name}/{method-name}",
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
        );
    }
}
