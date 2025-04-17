package com.shm.demo.dto;

import com.shm.demo.entity.CooperationType;
import com.shm.demo.entity.EnterpriseType;
import lombok.Data;

@Data
public class SearchEnterpriseRequest {

    private String name; // 企业名称 (模糊搜索)

    private CooperationType cooperationType; // 合作类型 (精确匹配: SEND, RECEIVE)

    private EnterpriseType enterpriseType; // 企业类型 (精确匹配: MANUFACTURING, SERVICE, TECHNOLOGY)

    private String region; // 地区 (精确匹配: 北京, 广州, 上海)

    // 分页参数 (可选, 默认可以设为第1页，每页10条)
    private int pageNum = 1;
    private int pageSize = 10;
}