package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class StatsClient {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StatsClient(@Value("${client.url}") String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                                         .queryParam("start", start.format(dateTimeFormatter))
                                         .queryParam("end", end.format(dateTimeFormatter))
                                         .queryParam("uris", (Object[]) uris)
                                         .queryParam("unique", unique)
                                         .toUriString();
        ResponseEntity<List<ViewStats>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStats>>() {});

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