package com.taai.app.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestPayload<T> {
    @NotNull
    private T data;
    @Builder.Default
    private Metadata metadata = new Metadata();

    @Getter
    @Setter
    static class Metadata {
        private final String uuid = UUID.randomUUID().toString();
        private final Date serverTime = new Date();
        private Marker marker = MarkerFactory.getMarker(uuid);
    }
}
