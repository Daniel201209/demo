package com.shm.demo.entity;

import lombok.Data;
import java.time.LocalDate; // 使用 LocalDate 处理日期
import java.time.LocalDateTime;

@Data
public class Cooperation {
    private Long id;
    private String cooperationTheme;
    private String initiatorRegion;
    private String receiverRegion;
    private LocalDate cooperationStartDate; // 日期
    private LocalDate cooperationEndDate;   // 日期
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted; // 0 or 1
}