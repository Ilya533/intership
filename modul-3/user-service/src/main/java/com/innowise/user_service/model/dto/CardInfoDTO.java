package com.innowise.user_service.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CardInfoDTO {
    private Long id;

    @NotNull(message = "Card number is mandatory")
    @Positive(message = "Card number must be positive")
    private Long number;

    @NotBlank(message = "Card holder is mandatory")
    @Size(min = 2, max = 100, message = "Holder name must be between 2 and 100  characters")
    private String holder;

    @NotNull(message = "Expiration date is mandatory")
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;

    private Long userId;
}