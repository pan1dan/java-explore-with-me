package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatsService;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) throws BadRequestException {
        LocalDateTime rightStart = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime rightEnd = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("GET /stats?start={}, end={}, uris={}, unique={}", rightStart, rightEnd, uris, unique);
        List<ViewStatsDto> returnableList = statsService.getStats(rightStart, rightEnd, uris, unique);
        log.info("GET /stats?start={}, end={}, uris={}, unique={}, return: {}", rightStart,
                rightEnd,
                uris,
                unique,
                returnableList);

        return returnableList;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("POST /hit, body: {}", endpointHitDto);
        EndpointHitDto returnableHit = statsService.saveHit(endpointHitDto);
        log.info("POST /hit, body: {}, return: {}", endpointHitDto, returnableHit);
        return returnableHit;

    }
}
