package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentDto;
import ru.practicum.comment.model.NewCommentDto;
import ru.practicum.comment.model.UpdateCommentDto;
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

    private final CommentRepository commentRepository;

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
            throw new ConflictException("Only admin can changed event in state PUBLISHED");
        }

        return EventMapper.fromEventToEventFullDto(eventRepository.save(updateEvent(event, updateEventUserRequest)));
    }

    private Event updateEvent(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Field: eventDate. " +
                                            "Error: должно содержать дату, которая еще не наступила и " +
                                            "отличаться от текущего времени минимум на два часа. " +
                                            "Value: " + updateEventUserRequest.getEventDate());
            }
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            if (updateEventUserRequest.getAnnotation().length() < 20
                    || updateEventUserRequest.getAnnotation().length() > 2000) {
                throw new ForbiddenException("Field annotation min length 20, max length 2000, " +
                        "now annotation_length=" + updateEventUserRequest.getAnnotation().length());
            }
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            if (updateEventUserRequest.getCategory() < 0) {
                throw new ForbiddenException("Category id must be greater than or equal to 0, " +
                        "now category=" + updateEventUserRequest.getCategory());
            }
            Category category = categoryRepository.findById(Long.valueOf(updateEventUserRequest.getCategory())).orElseThrow(() ->
                    new NotFoundException("Category with id= " + updateEventUserRequest.getCategory() + " was not found"));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            if (updateEventUserRequest.getDescription().length() < 20
                    || updateEventUserRequest.getDescription().length() > 7000) {
                throw new ForbiddenException("Field description min length 20, max length 7000, " +
                        "now description_length=" + updateEventUserRequest.getDescription().length());
            }
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getLocation() != null) {
            LocationDto locationDto = locationRepository.save(LocationMapper.fromLocationToLocationDto(updateEventUserRequest.getLocation()));
            event.setLocation(locationDto);
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW.name())) {
                event.setState(EventState.PENDING.name());
            } else if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW.name())) {
                event.setState(EventState.CANCELED.name());
            } else {
                throw new ForbiddenException("StateAction must be SEND_TO_REVIEW or CANCEL_REVIEW, " +
                        "now stateAction=" + updateEventUserRequest.getStateAction());
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            if (updateEventUserRequest.getTitle().length() < 3 || updateEventUserRequest.getTitle().length() > 120) {
                throw new ForbiddenException("Field title min length 3, max length 120, " +
                        "now title_length=" + updateEventUserRequest.getTitle().length());
            }
            event.setTitle(updateEventUserRequest.getTitle());
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
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Only event initiator can get requests");
        }

        return requestRepository.findAllRequestsByEventId(eventId);
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
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Only event initiator can get requests");
        }
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
        if (event.getParticipantLimit() != 0) {
            if (event.getParticipantLimit() - event.getConfirmedRequests() < 1) {
                throw new ConflictException("event participant limit has been exceeded");
            }
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
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
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

    @Override
    public CommentDto addUserComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id= " + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getState().equals(EventState.PUBLISHED.name())) {
            throw new ConflictException("The event must have a status of PUBLISHED to be able to add a comment");
        }
        Comment comment = CommentMapper.fromNewCommentDtoToComment(newCommentDto, event, userDto);
        comment = commentRepository.save(comment);
        return CommentMapper.fromCommentToCommentDto(comment);
    }

    @Override
    public CommentDto updateUserComment(Long userId, Long eventId, Long commentId, UpdateCommentDto updateCommentDto) {
        idValidation(userId, "userId");
        idValidation(eventId, "eventId");
        idValidation(commentId, "commentId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id= " + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id= " + commentId + " was not found"));
        if (!userId.equals(comment.getCommentator().getId())) {
            throw new ForbiddenException("Only commentator can change his comment");
        }

        comment.setText(updateCommentDto.getText());
        return CommentMapper.fromCommentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteUserComment(Long userId, Long commentId) {
        idValidation(userId, "userId");
        idValidation(commentId, "commentId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id= " + userId + " was not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id= " + commentId + " was not found"));
        if (!userId.equals(comment.getCommentator().getId())) {
            throw new ForbiddenException("Only commentator can delete his comment");
        }

        commentRepository.deleteById(commentId);
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Field " + fieldName + " must be greater than 0, " +
                    "now " + fieldName + "=" + id);
        }
    }
}
