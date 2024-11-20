package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {

    @Size(max = 2000, min = 20, message = "The annotation should contain from 20 to 2000 characters")
    private String annotation;

    private Long category;

    @Size(max = 7000, min = 20, message = "The description should contain from 20 to 7000 characters")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(max = 120, min = 3, message = "The title should contain from 3 to 120 characters")
    private String title;
}
