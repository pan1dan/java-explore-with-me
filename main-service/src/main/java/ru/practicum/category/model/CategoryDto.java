package ru.practicum.category.model;

import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    @Size(min = 1, max = 50, message = "The name should contain from 1 to 50 characters")
    private String name;
}
