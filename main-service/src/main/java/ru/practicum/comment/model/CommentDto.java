package ru.practicum.comment.model;

import lombok.*;
import ru.practicum.event.model.EventShortDto;
import ru.practicum.user.model.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    private EventShortDto event;

    private String text;

    private UserShortDto commentator;

    private LocalDateTime createdOn;
}
