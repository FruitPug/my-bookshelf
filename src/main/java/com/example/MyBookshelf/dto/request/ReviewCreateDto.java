package com.example.MyBookshelf.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewCreateDto {
    @NotBlank(message="Comment is required")
    private String comment;

    @Min(value=1, message="Rating must be ≥ 1")
    @Max(value=10, message="Rating must be ≤ 10")
    private double rating;
}
