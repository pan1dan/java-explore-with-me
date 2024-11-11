package ru.practicum;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.EndpointHitDto;

public class StatsMapper {
    public static EndpointHit fromEndpointHitDtoToEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                          .id(null)
                          .uri(endpointHitDto.getUri())
                          .ip(endpointHitDto.getIp())
                          .app(endpointHitDto.getApp())
                          .timestamp(endpointHitDto.getTimestamp())
                          .build();
    }

    public static EndpointHitDto fromEndpointHitToEndpointHitDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                             .uri(endpointHit.getUri())
                             .ip(endpointHit.getIp())
                             .app(endpointHit.getApp())
                             .timestamp(endpointHit.getTimestamp())
                             .build();
    }
}
