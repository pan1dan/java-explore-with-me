package ru.practicum.category.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    @Size(max = 50, min = 1, message = "The name should contain from 1 to 50 characters")
    @NotBlank(message = "must not be blank")
    private String name;
}
