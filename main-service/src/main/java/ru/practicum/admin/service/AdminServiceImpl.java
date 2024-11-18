package ru.practicum.admin.service;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.interfaces.AdminService;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.model.NewCategoryDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.*;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.repository.CompilationsEventsRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.*;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.LocationDto;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.NewUserRequest;
import ru.practicum.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CompilationsEventsRepository compilationsEventsRepository;
    private final CompilationRepository compilationRepository;
    private final LocationRepository locationRepository;

    @Transactional
    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        try {
            return categoryRepository.save(CategoryMapper.fromNewCategoryDtoToCategoryDto(newCategoryDto));
        } catch (ConstraintViolationException e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public void deleteCategoryById(Long catId) {
        idValidation(catId, "catId");
        CategoryDto categoryDto = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id=" + catId + " was not found"));

        List<EventShortDto> eventShortDto = eventRepository.findFirstEventByCategoryId(catId, PageRequest.of(0, 1));
        if (!eventShortDto.isEmpty()) {
            throw new ConditionsNotMetException("The category is not empty");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategoryById(Long catId, CategoryDto updateCategory) {
        idValidation(catId, "catId");
        CategoryDto categoryDto = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id= " + catId + " was not found"));
        categoryDto.setName(updateCategory.getName());
        try {
            return categoryRepository.save(categoryDto);
        } catch (ConstraintViolationException e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public List<EventShortDto> searchEventByCondition(List<Long> usersIds,
                                                      List<String> eventsStates,
                                                      List<Long> categoriesIds,
                                                      LocalDateTime startDate,
                                                      LocalDateTime endDate,
                                                      Integer from,
                                                      Integer size) {
        if (usersIds != null) {
            for (Long id : usersIds) {
                idValidation(id, "userId");
            }
        }
        if (from < 0) {
            throw new BadRequestException("Request parameter from must be greater than 0, now from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Request parameter 'size' must be greater than 0, now size=" + size);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.searchEvents(usersIds, eventsStates, categoriesIds, startDate, endDate, pageable);
    }

    @Override
    public EventFullDto editingEventById(Long eventId, UpdateEventAdminRequest updateEvent) {
        idValidation(eventId, "eventId");
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id= " + eventId + " was not found"));
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.PUBLISH_EVENT.name())) {
                if (!oldEvent.getState().equals(EventState.PENDING.name())) {
                    throw new ConflictException("Cannot publish the event because it's not in the right state: "
                                                                                                + oldEvent.getState());
                } else {
                   oldEvent.setState(EventState.PUBLISHED.name());
                }
            }
            if (updateEvent.getStateAction().equals((StateAction.REJECT_EVENT.name()))) {
                if (oldEvent.getState().equals(EventState.PUBLISHED.name())) {
                    throw new ConflictException("Cannot reject the event because it's not in the right state: "
                                                                                                + oldEvent.getState());
                } else {
                    oldEvent.setState(EventState.CANCELED.name());
                }
            }
        }
        if (updateEvent.getEventDate() != null) {
            if (oldEvent.getState().equals(EventState.PUBLISHED.name())
                    && oldEvent.getPublishedOn().plusHours(1).isAfter(updateEvent.getEventDate())) {
                throw new ConflictException("The start date of the modified event must be no earlier than one hour" +
                                                                                        " from the publication date");
            }
            oldEvent.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {

            oldEvent.setCategory(categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                    new ForbiddenException("Category with id= " + updateEvent.getCategory() + " was not found")));
        }
        if (updateEvent.getDescription() != null) {
            oldEvent.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            LocationDto locationDto = locationRepository.save(LocationMapper.fromLocationToLocationDto(updateEvent.getLocation()));
            oldEvent.setLocation(locationDto);
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            oldEvent.setTitle(updateEvent.getTitle());
        }

        return EventMapper.fromEventToEventFullDto(eventRepository.save(oldEvent));
    }

    @Override
    public List<UserDto> getUsersInformationByCondition(List<Long> usersIds, Integer from, Integer size) {
        if (usersIds != null) {
            for (Long userId : usersIds) {
                idValidation(userId, "userId");
            }
        }
        if (from < 0) {
            throw new BadRequestException("Request parameter from must be greater than 0, now from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Request parameter 'size' must be greater than 0, now size=" + size);
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return userRepository.findAllUsersByCondition(usersIds, pageable);
    }

    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        UserDto newUser = UserMapper.fromNewUserRequestToUserDto(newUserRequest);
        return userRepository.save(newUser);
    }

    @Override
    public void deleteUser(Long userId) {
        idValidation(userId, "userId");
        UserDto userDto = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id= "
                + userId + " was not found"));
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public CompilationDto addNewCompilations(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = compilationRepository.save(CompilationMapper.fromNewCompilationDtoToCompilation(newCompilationDto));
        CompilationDto resultCompilationDto = CompilationMapper.fromCompilationToCompilationDto(compilation);
        if (newCompilationDto.getEvents().isEmpty()) {
            resultCompilationDto.setEvents(new ArrayList<>());
            return resultCompilationDto;
        }

        List<Long> eventIdsList = newCompilationDto.getEvents();
        List<CompilationsEvents> compilationsEventsList = eventIdsList.stream()
                                        .map(eventId -> new CompilationsEvents(resultCompilationDto.getId(), eventId))
                                        .toList();
        compilationsEventsRepository.saveAll(compilationsEventsList);

        List<EventShortDto> eventShortDtoList = eventRepository.findAllEventsByIds(eventIdsList);
        resultCompilationDto.setEvents(eventShortDtoList);

        return resultCompilationDto;
    }

    @Override
    public void deleteCompilationById(Long compId) {
        idValidation(compId, "compId");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " was not found"));
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation) {
        idValidation(compId, "compId");
        Compilation oldCompilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " was not found"));
        if (updateCompilation.getPinned() != null) {
            oldCompilation.setPinned(updateCompilation.getPinned());
        }
        if (updateCompilation.getTitle() != null) {
            if (updateCompilation.getTitle().length() < 1 && updateCompilation.getTitle().length() > 50) {
                throw new ForbiddenException("Field annotation min length 1, max length 50, " +
                        "now annotation_length=" + updateCompilation.getTitle().length());
            }
            oldCompilation.setTitle(updateCompilation.getTitle());
        }
        compilationRepository.save(oldCompilation);
        CompilationDto resultDto = CompilationMapper.fromCompilationToCompilationDto(oldCompilation);
        resultDto.setEvents(new ArrayList<>());

        if (updateCompilation.getEvents() != null) {
            List<CompilationsEvents> oldCompilationsEventsList = compilationsEventsRepository.findAllCompilationsEventsByCompilationId(compId);
            List<Long> oldEventsIds = oldCompilationsEventsList.stream().map(CompilationsEvents::getEventId).toList();
            List<Long> newEventsIds = new ArrayList<>(updateCompilation.getEvents());
            List<Long> eventsIdsForDelete = new ArrayList<>();
            List<Long> eventsIdsForAdd = new ArrayList<>();

            if (newEventsIds.isEmpty() && oldEventsIds.isEmpty()) {
                return resultDto;

            } else if (!newEventsIds.isEmpty() && oldEventsIds.isEmpty()) {
                eventsIdsForAdd.addAll(newEventsIds);
            } else if (newEventsIds.isEmpty() && !oldEventsIds.isEmpty()) {
                eventsIdsForDelete.addAll(oldEventsIds);
            } else {
                eventsIdsForAdd = newEventsIds.stream()
                        .filter(id -> !oldEventsIds.contains(id))
                        .collect(Collectors.toList());

                eventsIdsForDelete = oldEventsIds.stream()
                        .filter(id -> !newEventsIds.contains(id))
                        .collect(Collectors.toList());
            }

            if (!eventsIdsForDelete.isEmpty()) {
                compilationsEventsRepository.deleteAllByEventsIds(eventsIdsForDelete);
            }
            if (!eventsIdsForAdd.isEmpty()) {
                List<CompilationsEvents> newCompilationsEventsList = eventsIdsForAdd.stream()
                        .map(eventId -> new CompilationsEvents(compId, eventId))
                        .toList();
                compilationsEventsRepository.saveAll(newCompilationsEventsList);

            }
            List<EventShortDto> eventShortDtoList = eventRepository.findAllEventsByIds(newEventsIds);
            resultDto.setEvents(eventShortDtoList);
        }

        return resultDto;
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Field " + fieldName + " must be greater than 0, " +
                    "now " + fieldName + "=" + id);
        }
    }

}