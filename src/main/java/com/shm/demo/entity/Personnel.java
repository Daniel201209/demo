package com.shm.demo.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Personnel {

    private Long id; // 主键ID

    private String name; // 姓名

    private Byte gender; // 性别 (例如: 1=男, 2=女)

    private Integer age; // 年龄

    private String phone; // 手机号

    private Byte education; // 学历 (枚举值)

    private LocalDate startWorkDate; // 参加工作时间

    private Long enterpriseId; // 任职企业ID

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间

    private Integer deleted = 0; // 逻辑删除标记 (0=未删除, 1=已删除), 保持和 Enterprise 一致用 Integer
}