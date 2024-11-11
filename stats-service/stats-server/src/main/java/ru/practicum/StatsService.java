package ru.practicum;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);

    EndpointHitDto saveHit(EndpointHitDto endpointHitDto);
}
