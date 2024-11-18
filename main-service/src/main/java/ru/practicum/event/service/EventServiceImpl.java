package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.event.interfaces.EventService;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventFullDto;
import ru.practicum.event.model.EventShortDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final StatsClient statsClient;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public List<EventShortDto> getEventsByCondition(String text,
                                                    List<Long> categoriesIds,
                                                    Boolean isPaid,
                                                    String startDate,
                                                    String endDate,
                                                    Boolean isAvailable,
                                                    String sort,
                                                    Integer from,
                                                    Integer size,
                                                    HttpServletRequest request) {
        if (from < 0) {
            throw new BadRequestException("Request parameter from must be greater than 0, now from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Request parameter 'size' must be greater than 0, now size=" + size);
        }
        if (categoriesIds != null) {
            for (Long categoryId : categoriesIds) {
                idValidation(categoryId, "categoryId");
            }
        }
        Pageable pageable = PageRequest.of(from / size, size);
        String sortField = sort;
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                sortField = "eventDate";
            } else if (sort.equals("VIEWS")) {
                sortField = "views";
            } else {
                throw new BadRequestException("Incorrectly field sort");
            }
        }
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.saveHit(endpointHitDto);
        return eventRepository.findAllEventsByFilter(text,
                                                     categoriesIds,
                                                     isPaid,
                                                     startDate,
                                                     endDate,
                                                     isAvailable,
                                                     sortField,
                                                     pageable,
                                                     EventState.PUBLISHED.name());
    }

    @Transactional
    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        idValidation(eventId, "eventId");
        Event resultEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        resultEvent.setViews(resultEvent.getViews() + 1);
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        return EventMapper.fromEventToEventFullDto(eventRepository.save(resultEvent));
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Field " + fieldName + " must be greater than 0, " +
                    "now " + fieldName + "=" + id);
        }
    }

}
