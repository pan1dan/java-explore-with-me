package ru.practicum.compilation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    @UniqueElements
    private List<Long> events;

    private Boolean pinned;

    @NotNull
    @NotBlank
    @Size(max = 50, min = 1, message = "The title should contain from 1 to 50 characters")
    private String title;
}
