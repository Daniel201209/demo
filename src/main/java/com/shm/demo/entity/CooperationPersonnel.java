package com.shm.demo.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CooperationPersonnel {
    private Long id;
    private Long cooperationId;
    private Long sendingEnterpriseId;
    private Long personnelId;
    private CooperationJobType cooperationJobType; // 枚举
    private Long receivingEnterpriseId;
    private LocalDate personnelStartDate; // 日期
    private LocalDate personnelEndDate;   // 日期
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 可以添加关联对象的引用，如果需要的话 (需要 Mapper 进行关联查询)
    // private Enterprise sendingEnterprise;
    // private Personnel personnel;
    // private Enterprise receivingEnterprise;
}