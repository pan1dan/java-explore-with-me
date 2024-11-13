package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStatsDto> statsList;
        if (unique && uris != null) {
            statsList = statsRepository.findAllStatsWithUniqueAndUris(start, end, uris);
        } else if (!unique && uris == null) {
            statsList = statsRepository.findAllStats(start, end);
        } else if (uris == null) {
            statsList = statsRepository.findAllStatsWithUnique(start, end);
        } else {
            statsList = statsRepository.findAllStatsWithUris(start, end, uris);
        }
        return statsList;
    }

    @Override
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit hit = statsRepository.save(StatsMapper.fromEndpointHitDtoToEndpointHit(endpointHitDto));
        return StatsMapper.fromEndpointHitToEndpointHitDto(hit);
    }
}
