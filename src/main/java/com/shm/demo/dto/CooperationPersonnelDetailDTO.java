package com.shm.demo.dto;

import java.time.LocalDate;

public class CooperationPersonnelDetailDTO {
    private String personnelName; // 姓名
    private String employingEnterpriseName; // 任职企业
    private String cooperationType; // 合作类型
    private String cooperationRegion; // 合作地区
    private String cooperationEnterpriseName; // 合作企业
    private LocalDate cooperationStartDate; // 合作开始时间
    private LocalDate cooperationEndDate; // 拟合作结束时间

    // Getters and Setters
    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public String getEmployingEnterpriseName() {
        return employingEnterpriseName;
    }

    public void setEmployingEnterpriseName(String employingEnterpriseName) {
        this.employingEnterpriseName = employingEnterpriseName;
    }

    public String getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(String cooperationType) {
        this.cooperationType = cooperationType;
    }

    public String getCooperationRegion() {
        return cooperationRegion;
    }

    public void setCooperationRegion(String cooperationRegion) {
        this.cooperationRegion = cooperationRegion;
    }

    public String getCooperationEnterpriseName() {
        return cooperationEnterpriseName;
    }

    public void setCooperationEnterpriseName(String cooperationEnterpriseName) {
        this.cooperationEnterpriseName = cooperationEnterpriseName;
    }

    public LocalDate getCooperationStartDate() {
        return cooperationStartDate;
    }

    public void setCooperationStartDate(LocalDate cooperationStartDate) {
        this.cooperationStartDate = cooperationStartDate;
    }

    public LocalDate getCooperationEndDate() {
        return cooperationEndDate;
    }

    public void setCooperationEndDate(LocalDate cooperationEndDate) {
        this.cooperationEndDate = cooperationEndDate;
    }
}