package io.api.event.util.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 해당 Annotation을 적용할 대상을 Method로 지정
@Retention(RetentionPolicy.SOURCE) // 해당 Annotation이 유지 되는 범위 지정 (Source 내에서만 유지)
//주석을 이용하여 TC의 내용을 명시할 수 있지만,
//추후 jUnit5로 변경하게 될 경우, 해당 Annotation을 jUnit5에서 제공하는 Annotation으로 일괄 변경가능하므로
//Custon Annotation을 생성하여 TC의 내용을 명시한다.
public @interface TestDescription {

    //Annotation에 설정되는 String type을 지정
    String value();
}
