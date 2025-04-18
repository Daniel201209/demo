package com.shm.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content; // 当前页数据列表
    private int page;        // 当前页码 (从1开始)
    private int size;        // 每页数量
    private long totalElements; // 总记录数
    private int totalPages;   // 总页数
}