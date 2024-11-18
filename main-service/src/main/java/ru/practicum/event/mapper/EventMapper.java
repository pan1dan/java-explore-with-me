package ru.practicum.event.mapper;

import ru.practicum.category.model.CategoryDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventFullDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.NewEventDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.UserDto;

import java.time.LocalDateTime;

public class EventMapper {

        public static Event fromNewEventDtoToEvent(NewEventDto newEventDto,
                                                   CategoryDto categoryDto,
                                                   UserDto user) {
        return Event.builder()
                .id(null)
                .annotation(newEventDto.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING.name())
                .title(newEventDto.getTitle())
                .views(0)
                .build();
    }

    public static EventFullDto fromEventToEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.fromUserDtoToUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

}
