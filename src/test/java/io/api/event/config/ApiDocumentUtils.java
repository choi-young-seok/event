package io.api.event.config;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public interface ApiDocumentUtils {

    static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(
                // 문서상 uri 를 기본값인 http://localhost:8080 에서 https://docs.api.com 으로 변경하기 위해 사용
                modifyUris()
                        .scheme("https")
                        .host("docs.api.com")
                        .removePort(),
                prettyPrint()); // 문서의 request 을 예쁘게 출력하기 위해 사용
    }

    static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint()); // 문서의 response 를 예쁘게 출력하기 위해 사용
    }
}
