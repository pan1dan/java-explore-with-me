package ru.practicum.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@Data
public class ViewStatsDto {
    String app;
    String uri;
    Long hits;

    public ViewStatsDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
