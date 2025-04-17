package com.shm.demo.service;

import com.shm.demo.entity.Enterprise;
import java.util.List;

public interface EnterpriseService {

    boolean isNameUnique(String name);

    Enterprise addEnterprise(Enterprise enterprise) throws IllegalArgumentException;

    List<Enterprise> getAllEnterprises();

    Enterprise getEnterpriseById(Long id); // 新增按 ID 查询

    Enterprise updateEnterprise(Enterprise enterprise) throws IllegalArgumentException; // 新增更新

    void deleteEnterprise(Long id); // 新增删除
}