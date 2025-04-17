package com.shm.demo.entity;

import lombok.Data;

@Data
public class Enterprise {
    private Long id;
    private String name; // 企业名称
    private CooperationType cooperationType; // 合作类型
    private EnterpriseType enterpriseType; // 企业类型
    private String region; // 地区
    private Integer deleted = 0; 
}