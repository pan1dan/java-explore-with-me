package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT new ru.practicum.event.model.EventShortDto(e.id, e.annotation, e.category, e.confirmedRequests, " +
            "e.eventDate, new ru.practicum.user.model.UserShortDto(e.initiator.id, e.initiator.name), " +
            "e.paid, e.title, e.views) " +
            "FROM Event e " +
            "WHERE e.category.id = :categoryId ")
    List<EventShortDto> findFirstEventByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);


    @Query("SELECT new ru.practicum.event.model.EventShortDto(e.id, e.annotation, e.category, e.confirmedRequests, " +
            "e.eventDate, new ru.practicum.user.model.UserShortDto(e.initiator.id, e.initiator.name), " +
            "e.paid, e.title, e.views) " +
            "FROM Event e " +
            "WHERE e.initiator.id = :initiatorId " +
            "ORDER BY e.id ASC")
    List<EventShortDto> findAllEventsByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("SELECT new ru.practicum.event.model.EventShortDto(e.id, e.annotation, e.category, e.confirmedRequests, " +
           "e.eventDate, new ru.practicum.user.model.UserShortDto(e.initiator.id, e.initiator.name), " +
           "e.paid, e.title, e.views) " +
           "FROM Event e " +
           "WHERE (:usersIds IS NULL OR e.initiator.id IN :usersIds) " +
           "AND (:eventsStates IS NULL OR e.state IN :eventsStates) " +
           "AND (:categoriesIds IS NULL OR e.category.id IN :categoriesIds) " +
           "AND (:startDate IS NULL OR e.eventDate >= :startDate) " +
           "AND (:endDate IS NULL OR e.eventDate <= :endDate) " +
           "ORDER BY e.id ASC")
    List<EventShortDto> searchEvents(@Param("usersIds") List<Long> usersIds,
                                     @Param("eventsStates") List<String> eventsStates,
                                     @Param("categoriesIds") List<Long> categoriesIds,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);

    @Query("SELECT new ru.practicum.event.model.EventShortDto(e.id, e.annotation, e.category, e.confirmedRequests, " +
           "e.eventDate, new ru.practicum.user.model.UserShortDto(e.initiator.id, e.initiator.name), " +
           "e.paid, e.title, e.views) " +
           "FROM Event e " +
           "WHERE e.id IN :eventIds")
    List<EventShortDto> findAllEventsByIds(@Param("eventIds") List<Long> eventIds);

    @Query("SELECT e " +
           "FROM Event e " +
           "WHERE e.state = :state " +
           "AND LOWER(e.annotation) like LOWER(:text) " +
           "AND e.category.id IN :categoriesIds " +
           "AND (:paid IS NULL OR e.paid = :paid) " +
           "ORDER BY :sort ASC")
    List<Event> findAllEventsByFilterWithoutTime(@Param("text") String textA,
                                              @Param("categoriesIds") List<Long> categoriesIds,
                                              @Param("paid") Boolean isPaid,
                                              Pageable pageable,
                                              @Param("state") String state,
                                              @Param("sort") String sort);

    @Query("SELECT e " +
           "FROM Event e " +
           "WHERE e.state = :state " +
           "AND LOWER(e.annotation) like LOWER(:text) " +
           "AND e.category.id IN :categoriesIds " +
           "AND (:paid IS NULL OR e.paid = :paid) " +
           "AND e.eventDate BETWEEN :startDate AND :endDate " +
           "ORDER BY :sort ASC")
    List<Event> findAllEventsByFilter(@Param("text") String text,
                                      @Param("categoriesIds") List<Long> categoriesIds,
                                      @Param("paid") Boolean isPaid,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable,
                                      @Param("state") String state,
                                      @Param("sort") String sort);

//    @Query("SELECT new ru.practicum.event.model.EventShortDto(e.id, e.annotation, e.category, e.confirmedRequests, " +
//            "e.eventDate, new ru.practicum.user.model.UserShortDto(e.initiator.id, e.initiator.name), " +
//            "e.paid, e.title, e.views) " +
//            "FROM Event e " +
//            "WHERE e.state = :state " +
//            "AND (:text IS NULL OR (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
//            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))) " +
//            "AND (:categoriesIds IS NULL OR e.category.id IN :categoriesIds) " +
//            "AND (:isPaid IS NULL OR e.paid = :isPaid) " +
//            "AND (:startDate IS NULL OR e.eventDate >= :startDate) " +
//            "AND (:endDate IS NULL OR e.eventDate <= :endDate) " +
//            "AND (:isAvailable IS NULL OR e.confirmedRequests < e.participantLimit) " +
//            "ORDER BY CASE WHEN :sort = 'eventDate' THEN e.eventDate END ASC, " +
//            "CASE WHEN :sort = 'views' THEN e.views END ASC")
//    List<EventShortDto> findAllEventsByFilter(@Param("text") String text,
//                                              @Param("categoriesIds") List<Long> categoriesIds,
//                                              @Param("isPaid") Boolean isPaid,
//                                              @Param("startDate") LocalDateTime startDate,
//                                              @Param("endDate") LocalDateTime endDate,
//                                              @Param("isAvailable") Boolean isAvailable,
//                                              @Param("sort") String sort,
//                                              Pageable pageable,
//                                              @Param("state") String state);


    @Query("SELECT e " +
           "FROM Event e " +
           "JOIN CompilationsEvents ce ON ce.eventId = e.id " +
           "WHERE ce.compilationId = :compId")
    List<Event> findEventsByCompilationId(@Param("compId") Long compId);

    @Query("SELECT e " +
           "FROM Event e " +
           "WHERE e.id = :eventId")
    Optional<Event> findEventByEventId(@Param("eventId") Long eventId);
}