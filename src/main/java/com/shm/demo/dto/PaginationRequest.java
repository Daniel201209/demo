package com.shm.demo.dto;

import lombok.Data;
import javax.validation.constraints.Min;

@Data
public class PaginationRequest {
    // 页码，从 1 开始 (或者从 0 开始，取决于实现)
    @Min(value = 1, message = "页码必须大于等于1")
    private int page = 1; // 默认第一页

    // 每页数量
    @Min(value = 1, message = "每页数量必须大于等于1")
    private int size = 10; // 默认每页10条
}