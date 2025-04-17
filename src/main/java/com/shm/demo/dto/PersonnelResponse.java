package com.shm.demo.dto;

import com.shm.demo.entity.Personnel;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PersonnelResponse {
    private Long id;
    private String name;
    private Byte gender;
    private Integer age;
    private String phone;
    private Byte education;
    private LocalDate startWorkDate;
    private Long enterpriseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 从 Entity 转换的静态工厂方法
    public static PersonnelResponse fromEntity(Personnel entity) {
        if (entity == null) {
            return null;
        }
        PersonnelResponse dto = new PersonnelResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setGender(entity.getGender());
        dto.setAge(entity.getAge());
        dto.setPhone(entity.getPhone());
        dto.setEducation(entity.getEducation());
        dto.setStartWorkDate(entity.getStartWorkDate());
        dto.setEnterpriseId(entity.getEnterpriseId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}