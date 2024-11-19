package ru.practicum.event.model;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.location.model.Location;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    Integer category;
    @Size(min = 20, max = 7000)
    String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
    @Size(min = 3, max = 120)
    String title;
//    String annotation;
//    Integer category;
//    String description;
//    String eventDate;
//    Location location;
//    Boolean paid;
//    Integer participantLimit;
//    Boolean requestModeration;
//    String stateAction;
//    String title;
}
