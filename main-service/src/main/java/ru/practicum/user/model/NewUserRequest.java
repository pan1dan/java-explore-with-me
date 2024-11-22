package ru.practicum.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "must not be blank")
    @Email
    @Size(max = 254, min = 6, message = "The email should contain from 6 to 254 characters")
    private String email;

    @NotBlank(message = "must not be blank")
    @Size(max = 250, min = 2, message = "The name should contain from 2 to 250 characters")
    private String name;
}
