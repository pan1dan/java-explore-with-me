package ru.practicum.compilation.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    @UniqueElements
    List<Long> events;
    Boolean pinned;
    @Size(max = 50, min = 1, message = "The title should contain from 1 to 50 characters")
    String title;
}
