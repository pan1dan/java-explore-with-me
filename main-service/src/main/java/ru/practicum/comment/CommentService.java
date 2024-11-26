package ru.practicum.comment;

import ru.practicum.comment.model.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllEventCommentaries(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);
}
