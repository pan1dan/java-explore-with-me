package ru.practicum.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "hits")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    Long id;
    @Column(name = "app", nullable = false)
    String app;
    @Column(name = "uri", nullable = false)
    String uri;
    @Column(name = "ip", nullable = false)
    String ip;
    @Column(name = "timing", nullable = false)
    LocalDateTime timestamp;
}
