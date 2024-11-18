package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank(message = "must not be blank")
    @Size(max = 2000, min = 20, message = "The annotation should contain from 20 to 2000 characters")
    String annotation;
    @NotNull(message = "must not be null")
    @Positive
    Long category;
    @NotBlank(message = "must not be blank")
    @Size(max = 7000, min = 20, message = "The description should contain from 20 to 7000 characters")
    String description;
    @NotNull(message = "must not be null")
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull(message = "must not be null")
    Location location;
    @NotNull(message = "must not be null")
    Boolean paid;
    @NotNull(message = "must not be null")
    @PositiveOrZero(message = "must be greater than or equal to 0")
    Integer participantLimit;
    @NotNull(message = "must not be null")
    Boolean requestModeration;
    @NotBlank(message = "must not be blank")
    @Size(max = 120, min = 3, message = "The title should contain from 3 to 120 characters")
    String title;
}
