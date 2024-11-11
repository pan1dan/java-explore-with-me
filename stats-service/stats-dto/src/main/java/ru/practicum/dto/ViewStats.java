package ru.practicum.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViewStats {
    String app;
    String uri;
    Long hits;
}
