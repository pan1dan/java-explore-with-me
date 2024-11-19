package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.LocationDto;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;
import ru.practicum.request.model.ParticipationRequestDto;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.interfaces.UserService;
import ru.practicum.user.model.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        idValidation(userId, "userId");
        if (from < 0) {
            throw new BadRequestException("Request parameter from must be greater than 0, now from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Request parameter 'size' must be greater than 0, now size=" + size);
        }
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id= " + userId + " was not found"));


        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllEventsByInitiatorId(userId, pageable);
    }

    @Override
    public EventFullDto addUserEvent(Long userId, NewEventDto newEventDto) {
        idValidation(userId, "userId");
        UserDto user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id= " + userId + " was not found"));
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Field: eventDate. " +
                    "Error: должно содержать дату, которая еще не наступила и " +
                    "отличаться от текущего времени минимум на два часа. " +
                    "Value: " + newEventDto.getEventDate());
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("Category with id= " + newEventDto.getCategory() + " was not found"));
        LocationDto locationDto = locationRepository.save(LocationMapper.fromLocationToLocationDto(newEventDto.getLocation()));
        Event event = EventMapper.fromNewEventDtoToEvent(newEventDto,
                                                         category,
                                                         user,
                                                         locationDto);


        return EventMapper.fromEventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id= " + userId +
                                                                                                    " was not found"));
        return EventMapper.fromEventToEventFullDto(eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id= " + eventId + " was not found")));
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Event date must before now date");
            }
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id= " + userId +
                " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id= " + eventId + " was not found"));
        if (event.getState().equals(EventState.PUBLISHED.name())) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }

        return EventMapper.fromEventToEventFullDto(eventRepository.save(updateEvent(event, updateEventUserRequest)));
    }

    private Event updateEvent(Event event, UpdateEventUserRequest uEUR) {
        if (uEUR.getEventDate() != null) {
            if (uEUR.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Field: eventDate. " +
                                            "Error: должно содержать дату, которая еще не наступила и " +
                                            "отличаться от текущего времени минимум на два часа. " +
                                            "Value: " + uEUR.getEventDate());
            }
            event.setEventDate(uEUR.getEventDate());
        }
        if (uEUR.getAnnotation() != null) {
            if (uEUR.getAnnotation().length() < 20 || uEUR.getAnnotation().length() > 2000) {
                throw new ForbiddenException("Field annotation min length 20, max length 2000, " +
                        "now annotation_length=" + uEUR.getAnnotation().length());
            }
            event.setAnnotation(uEUR.getAnnotation());
        }
        if (uEUR.getCategory() != null) {
            if (uEUR.getCategory() < 0) {
                throw new ForbiddenException("Category id must be greater than or equal to 0, " +
                        "now category=" + uEUR.getCategory());
            }
            Category category = categoryRepository.findById(Long.valueOf(uEUR.getCategory())).orElseThrow(() ->
                    new NotFoundException("Category with id= " + uEUR.getCategory() + " was not found"));
            event.setCategory(category);
        }
        if (uEUR.getDescription() != null) {
            if (uEUR.getDescription().length() < 20 || uEUR.getDescription().length() > 7000) {
                throw new ForbiddenException("Field description min length 20, max length 7000, " +
                        "now description_length=" + uEUR.getDescription().length());
            }
            event.setDescription(uEUR.getDescription());
        }
        if (uEUR.getLocation() != null) {
            LocationDto locationDto = locationRepository.save(LocationMapper.fromLocationToLocationDto(uEUR.getLocation()));
            event.setLocation(locationDto);
        }
        if (uEUR.getPaid() != null) {
            event.setPaid(uEUR.getPaid());
        }
        if (uEUR.getParticipantLimit() != null) {
            event.setParticipantLimit(uEUR.getParticipantLimit());
        }
        if (uEUR.getRequestModeration() != null) {
            event.setRequestModeration(uEUR.getRequestModeration());
        }
        if (uEUR.getStateAction() != null) {
            if (uEUR.getStateAction().equals(StateAction.SEND_TO_REVIEW.name())) {
                event.setState(EventState.PENDING.name());
            } else if (uEUR.getStateAction().equals(StateAction.CANCEL_REVIEW.name())) {
                event.setState(EventState.CANCELED.name());
            } else {
                throw new ForbiddenException("StateAction must be SEND_TO_REVIEW or CANCEL_REVIEW, " +
                        "now stateAction=" + uEUR.getStateAction());
            }
        }
        if (uEUR.getTitle() != null) {
            if (uEUR.getTitle().length() < 3 || uEUR.getTitle().length() > 120) {
                throw new ForbiddenException("Field title min length 3, max length 120, " +
                        "now title_length=" + uEUR.getTitle().length());
            }
            event.setTitle(uEUR.getTitle());
        }

        return event;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnUserEvent(Long userId, Long eventId) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id= "
                + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id= " + eventId + " was not found"));

        return requestRepository.findAllRequestsByUserIdAndEventId(userId, eventId);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsOnUserEvent(Long userId,
                                                                    Long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id= "
                + userId + " was not found"));
        Event event = eventRepository.findEventByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id= " + eventId + " was not found"));
        List<Long> requestsIds = updateRequest.getRequestIds();

        List<ParticipationRequestDto> requestDtoListBefore = requestRepository.findAllById(requestsIds);
        int confirmedRequests = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();

        if (participantLimit != 0 && ((participantLimit - confirmedRequests) == 0)) {
            throw new ConflictException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное " +
                                                                                                            "событие");
        }
        int mayBeAccept;
        if (participantLimit == 0 || !event.getRequestModeration()) {
            mayBeAccept = Integer.MAX_VALUE;
        } else {
            mayBeAccept = participantLimit - confirmedRequests;
        }

        List<ParticipationRequestDto> resultConfirmedRequestsList = new ArrayList<>();
        List<ParticipationRequestDto> resultRejectedRequestsList = new ArrayList<>();

        if (updateRequest.getStatus().equals(RequestStatus.REJECTED.name())) {
            for (ParticipationRequestDto requestDto : requestDtoListBefore) {
                if (!requestDto.getStatus().equals(RequestStatus.PENDING.name())) {
                    throw new BadRequestException("Все заявки должны быть в статусе PENDING");
                } else {
                    requestDto.setStatus(RequestStatus.REJECTED.name());
                    resultRejectedRequestsList.add(requestDto);
                }
            }
        } else {
            for (ParticipationRequestDto requestDto : requestDtoListBefore) {
                if (mayBeAccept == 0) {
                    requestDto.setStatus(RequestStatus.REJECTED.name());
                    resultRejectedRequestsList.add(requestDto);
                } else {
                    if (!requestDto.getStatus().equals(RequestStatus.PENDING.name())) {
                        throw new BadRequestException("Все заявки должны быть в статусе PENDING");
                    } else {
                        requestDto.setStatus(RequestStatus.CONFIRMED.name());
                        resultConfirmedRequestsList.add(requestDto);
                        confirmedRequests++;
                        mayBeAccept--;
                    }
                }
            }
        }
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
        List<ParticipationRequestDto> updateRequests = new ArrayList<>(resultConfirmedRequestsList);
        updateRequests.addAll(resultRejectedRequestsList);
        requestRepository.saveAll(updateRequests);

        return EventRequestStatusUpdateResult.builder()
                                             .rejectedRequests(resultRejectedRequestsList)
                                             .confirmedRequests(resultConfirmedRequestsList)
                                             .build();
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(Long userId) {
        idValidation(userId, "userId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id= "
                + userId + " was not found"));
        return requestRepository.findAllByRequesterId(userId);
    }

    @Override
    public ParticipationRequestDto addUserRequestOnEvent(Long userId, Long eventId) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id= "
                + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Request from this user on this event already exist");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Event initiator can't create request on his own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED.name())) {
            throw new ConflictException("Cannot participate in an unpublished event");
        }
        if (event.getParticipantLimit() != 0
                && (event.getParticipantLimit() - event.getConfirmedRequests()) == 0) {
            throw new ConflictException("event participant limit has been exceeded");
        }
        ParticipationRequestDto newRequest = ParticipationRequestDto.builder()
                .id(null)
                .created(LocalDateTime.now())
                .event(eventId)
                .requester(userId)
                .status(null)
                .build();
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            newRequest.setStatus(RequestStatus.PENDING.name());
        } else {
            newRequest.setStatus(RequestStatus.CONFIRMED.name());
        }
        return requestRepository.save(newRequest);
    }

    @Override
    public ParticipationRequestDto cancelUserRequestOnEvent(Long userId, Long requestId) {
        idValidation(userId, "userId");
        idValidation(requestId, "requestId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id= " + userId + " was not found"));
        ParticipationRequestDto request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("UserRequest with id= " + requestId + " was not found"));
        request.setStatus(RequestStatus.CANCELED.name());
        return requestRepository.save(request);
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Field " + fieldName + " must be greater than 0, " +
                    "now " + fieldName + "=" + id);
        }
    }

}
