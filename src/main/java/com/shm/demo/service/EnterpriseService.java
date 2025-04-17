package com.shm.demo.service;

import com.github.pagehelper.PageInfo; // 引入 PageInfo
import com.shm.demo.dto.SearchEnterpriseRequest; // 引入请求 DTO
import com.shm.demo.entity.Enterprise;
import java.util.List;

public interface EnterpriseService {

    boolean isNameUnique(String name);

    Enterprise addEnterprise(Enterprise enterprise) throws IllegalArgumentException;

    List<Enterprise> getAllEnterprises();

    Enterprise getEnterpriseById(Long id);

    Enterprise updateEnterprise(Enterprise enterprise) throws IllegalArgumentException;

    void deleteEnterprise(Long id);

    /**
     * 根据条件搜索企业信息 (分页)
     * @param request 包含搜索条件和分页参数的请求对象
     * @return 包含分页信息的企业列表 (PageInfo 对象)
     */
    PageInfo<Enterprise> searchEnterprises(SearchEnterpriseRequest request); // 返回 PageInfo
}