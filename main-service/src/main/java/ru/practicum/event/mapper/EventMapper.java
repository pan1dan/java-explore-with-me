package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.*;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.LocationDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.UserDto;

import java.time.LocalDateTime;

public class EventMapper {

        public static Event fromNewEventDtoToEvent(NewEventDto newEventDto,
                                                   Category category,
                                                   UserDto user,
                                                   LocationDto locationDto) {
        return Event.builder()
                .id(null)
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(locationDto)
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
                .category(CategoryMapper.fromCategoryToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.fromUserDtoToUserShortDto(event.getInitiator()))
                .location(LocationMapper.fromLocationDtoToLocation(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto fromEventToEventShortDto(Event event) {
        return EventShortDto.builder()
                            .id(event.getId())
                            .annotation(event.getAnnotation())
                            .category(CategoryMapper.fromCategoryToCategoryDto(event.getCategory()))
                            .confirmedRequests(event.getConfirmedRequests())
                            .eventDate(event.getEventDate())
                            .initiator(UserMapper.fromUserDtoToUserShortDto(event.getInitiator()))
                            .paid(event.getPaid())
                            .title(event.getTitle())
                            .views(event.getViews())
                            .build();
    }


}
