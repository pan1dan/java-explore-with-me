package ru.practicum.client;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ClientController {
    private final StatsClient statClient;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        LocalDateTime rightStart = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime rightEnd = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("GET /stats?start={}, end={}, uris={}, unique={}", rightStart, rightEnd, uris, unique);
        List<ViewStats> returnableList = statClient.getStats(rightStart, rightEnd, uris, unique);
        log.info("GET /stats?start={}, end={}, uris={}, unique={}, return: {}", rightStart,
                rightEnd,
                uris,
                unique,
                returnableList);

        return returnableList;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("POST /hit, body: {}", endpointHitDto);
        ResponseEntity<Object> returnableHit = statClient.saveHit(endpointHitDto);
        log.info("POST /hit, body: {}, return: {}", endpointHitDto, returnableHit);
        return returnableHit;

    }
}
