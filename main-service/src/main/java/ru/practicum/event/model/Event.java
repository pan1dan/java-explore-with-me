package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.location.model.LocationDto;
import ru.practicum.user.model.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    Long id;
    @Column(name = "annotation")
    String annotation;
    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    Category category;
    @Column(name = "confirmed_requests")
    Integer confirmedRequests;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "description")
    String description;
    @Column(name = "event_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @JoinColumn(name = "initiator_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    UserDto initiator;
    @JoinColumn(name = "location_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    LocationDto location;
    @Column(name = "paid")
    Boolean paid;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Column(name = "state")
    String state;
    @Column(name = "title")
    String title;
    @Column(name = "views")
    Integer views;
}
