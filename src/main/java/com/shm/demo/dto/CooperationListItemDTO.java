package com.shm.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CooperationListItemDTO {
    private Long id; // 合作信息 ID，用于操作
    private String cooperationTheme; // 合作主题
    private String initiatorRegion; // 发起方
    private String receiverRegion; // 接收方
    private Integer personnelCount; // 合作人数
    private LocalDate cooperationStartDate; // 合作开始时间
    private LocalDate cooperationEndDate; // 拟合作结束时间
    private LocalDateTime createdAt; // 创建时间
}