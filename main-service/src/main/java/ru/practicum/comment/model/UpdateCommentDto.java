package ru.practicum.comment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCommentDto {

    @NotBlank(message = "The only parameter cannot be empty or null")
    @Size(max = 5000, min = 20, message = "Comment text should contain from 20 to 5000 characters")
    private String text;
}
