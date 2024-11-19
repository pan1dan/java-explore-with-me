package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class StatsClient {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate restTemplate;
    @Value("${client.url}")
    private String baseUrl;

    @Autowired
    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/stats")
                .queryParam("start", start.format(dateTimeFormatter))
                .queryParam("end", end.format(dateTimeFormatter))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();
        ResponseEntity<List<ViewStatsDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {});

        return responseEntity.getBody();
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto endpointHitDto) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/hit").toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);

        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
    }
}
