package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c " +
           "FROM Comment c " +
           "WHERE c.commentator.id = :commentatorId ")
    List<Comment> findAllCommentariesByCommentatorId(@Param("commentatorId") Long commentatorId, Pageable pageable);

    @Query("SELECT c " +
           "FROM Comment c " +
           "WHERE c.event.id = :eventId ")
    List<Comment> findAllCommentariesByEventId(@Param("eventId") Long eventId, Pageable pageable);
}
