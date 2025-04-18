package com.shm.demo.entity;

public enum CooperationJobType {
    // --- 修改枚举常量，调用构造函数传入中文描述 ---
    MANAGEMENT("管理"), // 管理
    TECHNOLOGY("技术");  // 技术
    // --- 结束修改 ---

    // --- 新增字段、构造函数和 getter ---
    private final String description; // 用于存储中文描述的字段

    // 私有构造函数，用于初始化 description 字段
    CooperationJobType(String description) {
        this.description = description;
    }

    // 公共 getter 方法，用于获取中文描述
    public String getDescription() {
        return description;
    }
    // --- 结束新增 ---
}