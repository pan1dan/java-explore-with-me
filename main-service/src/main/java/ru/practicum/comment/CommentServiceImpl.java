package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentDto;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getAllEventCommentaries(Long eventId, Integer from, Integer size) {
        idValidation(eventId, "eventId");
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id= " + eventId + " was not found"));
        if (from < 0) {
            throw new BadRequestException("Request parameter from must be greater than 0, now from=" + from);
        }
        if (size < 0) {
            throw new BadRequestException("Request parameter 'size' must be greater than 0, now size=" + size);
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> commentList = commentRepository.findAllCommentariesByEventId(eventId, pageable);

        if (commentList.isEmpty()) {
            return new ArrayList<>();
        }
        return commentList.stream().map(CommentMapper::fromCommentToCommentDto).toList();
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        idValidation(commentId, "commentId");
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id= " + commentId + " was not found"));
        return CommentMapper.fromCommentToCommentDto(comment);
    }

    private void idValidation(Long id, String fieldName) {
        if (id < 0) {
            throw new BadRequestException("Field " + fieldName + " must be greater than 0, " +
                    "now " + fieldName + "=" + id);
        }
    }
}
