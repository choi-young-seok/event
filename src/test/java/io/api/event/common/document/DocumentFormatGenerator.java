package io.api.event.common.document;

import org.springframework.restdocs.snippet.Attributes;

public interface DocumentFormatGenerator {
    static Attributes.Attribute getDateFormat() {
        return Attributes.key("format").value("yyyy-MM-dd");
    }

    static Attributes.Attribute getDateTimeFormat() {
        return Attributes.key("format").value("yyyy-MM-dd HH:mm:ss");
    }
}
