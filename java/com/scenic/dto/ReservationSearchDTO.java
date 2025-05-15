package com.scenic.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationSearchDTO {
    private String username;
    private Integer status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String keyword;
} 