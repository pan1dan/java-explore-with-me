package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.dto.ViewStats (e.app, e.uri, COUNT(DISTINCT e.ip)) " +
           "FROM EndpointHit e " +
           "WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY COUNT(DISTINCT e.ip) DESC ")
    List<ViewStats> findAllStatsWithUniqueAndUris(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  @Param("uris") String[] uris);

    @Query("SELECT new ru.practicum.dto.ViewStats (e.app, e.uri, COUNT(DISTINCT e.ip)) " +
           "FROM EndpointHit e " +
           "WHERE e.timestamp BETWEEN :start AND :end " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY COUNT(DISTINCT e.ip) DESC ")
    List<ViewStats> findAllStatsWithUnique(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStats (e.app, e.uri, COUNT(e.ip)) " +
           "FROM EndpointHit e " +
           "WHERE e.timestamp BETWEEN :start AND :end " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY COUNT(e.ip) DESC ")
    List<ViewStats> findAllStats(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStats (e.app, e.uri, COUNT(e.ip)) " +
           "FROM EndpointHit e " +
           "WHERE e.timestamp BETWEEN :start AND :end AND e.uri IN :uris " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY COUNT(e.ip) DESC ")
    List<ViewStats> findAllStatsWithUris(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end,
                                         @Param("uris") String[] uris);

}
