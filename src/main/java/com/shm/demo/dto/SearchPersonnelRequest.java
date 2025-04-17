package com.shm.demo.dto;

import lombok.Data;

@Data
public class SearchPersonnelRequest {

    private String name; // 姓名 (模糊搜索)

    private Byte gender; // 性别 (精确匹配, 1=男, 2=女)

    private Long enterpriseId; // 任职企业ID (精确匹配)

    // 分页参数 (可选, 默认可以设为第1页，每页10条)
    private int pageNum = 1;
    private int pageSize = 10;

}