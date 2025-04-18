package com.shm.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class CooperationDetailDTO {
    private Long id;
    private String cooperationTheme; // 合作主题
    private String initiatorRegion; // 发起方
    private String receiverRegion; // 接收方
    private Integer personnelCount; // 合作人数
    private LocalDate cooperationStartDate; // 合作开始时间
    private LocalDate cooperationEndDate; // 拟合作结束时间
    private List<CooperationPersonnelDetailDTO> personnelList; // 合作人员列表

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCooperationTheme() {
        return cooperationTheme;
    }

    public void setCooperationTheme(String cooperationTheme) {
        this.cooperationTheme = cooperationTheme;
    }

    public String getInitiatorRegion() {
        return initiatorRegion;
    }

    public void setInitiatorRegion(String initiatorRegion) {
        this.initiatorRegion = initiatorRegion;
    }

    public String getReceiverRegion() {
        return receiverRegion;
    }

    public void setReceiverRegion(String receiverRegion) {
        this.receiverRegion = receiverRegion;
    }

    public Integer getPersonnelCount() {
        return personnelCount;
    }

    public void setPersonnelCount(Integer personnelCount) {
        this.personnelCount = personnelCount;
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

    public List<CooperationPersonnelDetailDTO> getPersonnelList() {
        return personnelList;
    }

    public void setPersonnelList(List<CooperationPersonnelDetailDTO> personnelList) {
        this.personnelList = personnelList;
    }
}