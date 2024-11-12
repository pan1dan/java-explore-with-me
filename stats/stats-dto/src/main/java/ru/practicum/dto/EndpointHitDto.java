package ru.practicum.dto;

//import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EndpointHitDto {
    @NotBlank
    @NotNull
    String app;
    @NotBlank
    @NotNull
    String uri;
    @NotBlank
    @NotNull
    String ip;
    @NotBlank
    @NotNull
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}
