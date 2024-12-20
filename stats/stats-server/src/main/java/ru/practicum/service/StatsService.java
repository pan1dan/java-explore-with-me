package ru.practicum.service;

import org.apache.coyote.BadRequestException;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) throws BadRequestException;

    EndpointHitDto saveHit(EndpointHitDto endpointHitDto);
}

