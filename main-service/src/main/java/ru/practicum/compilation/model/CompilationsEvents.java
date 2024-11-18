package ru.practicum.compilation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compilations_events")
public class CompilationsEvents {
    @Id
    @Column(name = "compilation_id")
    Long compilationId;
    @Column(name = "event_id")
    Long eventId;
}
