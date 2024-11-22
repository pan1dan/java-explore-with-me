package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.user.model.UserDto;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDto, Long> {

    @Query("SELECT u " +
           "FROM UserDto u " +
           "WHERE (:usersIds IS NULL OR u.id IN :usersIds)")
    List<UserDto> findAllUsersByCondition(@Param("usersIds") List<Long> usersIds, Pageable pageable);

}
