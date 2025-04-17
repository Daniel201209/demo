package com.shm.demo.service.impl;

import com.github.pagehelper.PageHelper; // 引入 PageHelper
import com.github.pagehelper.PageInfo;   // 引入 PageInfo
import com.shm.demo.dto.SearchEnterpriseRequest; // 引入请求 DTO
import com.shm.demo.entity.Enterprise;
import com.shm.demo.mapper.EnterpriseMapper;
import com.shm.demo.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // 引入 StringUtils

import java.util.Arrays; // 引入 Arrays
import java.util.List;
import java.util.Objects;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    // 允许的地区列表
    private static final List<String> ALLOWED_REGIONS = Arrays.asList("北京", "广州", "上海");

    @Override
    public boolean isNameUnique(String name) {
        return enterpriseMapper.findByName(name) == null;
    }

    @Override
    @Transactional
    public Enterprise addEnterprise(Enterprise enterprise) throws IllegalArgumentException {
        validateEnterprise(enterprise, false);
        enterpriseMapper.insert(enterprise);
        return enterprise; // insert 后 enterprise 对象会包含 ID
    }

    @Override
    public List<Enterprise> getAllEnterprises() {
        return enterpriseMapper.findAll();
    }

    @Override
    public Enterprise getEnterpriseById(Long id) {
        if (id == null) return null;
        return enterpriseMapper.findById(id);
    }

    @Override
    @Transactional
    public Enterprise updateEnterprise(Enterprise enterprise) throws IllegalArgumentException {
        if (enterprise == null || enterprise.getId() == null) {
            throw new IllegalArgumentException("更新时企业信息和ID不能为空");
        }
        // 确保不意外更新 deleted 状态
        enterprise.setDeleted(null);

        validateEnterprise(enterprise, true); // 使用更新校验

        int updatedRows = enterpriseMapper.update(enterprise);
        if (updatedRows == 0) {
            Enterprise rawExisting = enterpriseMapper.findRawById(enterprise.getId());
            if (rawExisting == null) {
                throw new IllegalArgumentException("未找到要更新的企业信息，ID: " + enterprise.getId());
            } else if (rawExisting.getDeleted() == 1) {
                throw new IllegalArgumentException("企业信息已被删除，无法更新，ID: " + enterprise.getId());
            } else {
                 throw new IllegalStateException("更新企业信息时发生未知错误，ID: " + enterprise.getId());
            }
        }
        return enterpriseMapper.findById(enterprise.getId()); // 返回更新后的信息
    }

    @Override
    @Transactional
    public void deleteEnterprise(Long id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("删除时企业ID不能为空");
        }
        int deletedRows = enterpriseMapper.softDeleteById(id);
        if (deletedRows == 0) {
            Enterprise rawExisting = enterpriseMapper.findRawById(id);
             if (rawExisting == null) {
                throw new IllegalArgumentException("未找到要删除的企业信息，ID: " + id);
            } else if (rawExisting.getDeleted() == 1) {
                 System.out.println("企业信息 ID: " + id + " 已被删除。");
            } else {
                 throw new IllegalStateException("删除企业信息时发生未知错误，ID: " + id);
            }
        }
    }

    // 辅助校验方法
    private void validateEnterprise(Enterprise enterprise, boolean isUpdate) throws IllegalArgumentException {
        if (enterprise == null) {
            throw new IllegalArgumentException("企业信息不能为空");
        }
        if (!StringUtils.hasText(enterprise.getName()) || enterprise.getName().length() > 20) {
            throw new IllegalArgumentException("企业名称不能为空且长度不能超过20");
        }
        if (enterprise.getCooperationType() == null) {
            throw new IllegalArgumentException("合作类型不能为空");
        }
        if (enterprise.getEnterpriseType() == null) {
            throw new IllegalArgumentException("企业类型不能为空");
        }
        // 校验地区是否在允许范围内 (如果地区非空)
        if (StringUtils.hasText(enterprise.getRegion()) && !ALLOWED_REGIONS.contains(enterprise.getRegion())) {
            throw new IllegalArgumentException("地区无效，只允许: " + String.join(", ", ALLOWED_REGIONS));
        }

        // 名称唯一性校验 (仅在新增时或更新了名称时)
        Enterprise existingByName = enterpriseMapper.findByName(enterprise.getName());
        if (existingByName != null && existingByName.getDeleted() == 0) {
            if (!isUpdate || !Objects.equals(existingByName.getId(), enterprise.getId())) {
                throw new IllegalArgumentException("企业名称已存在: " + enterprise.getName());
            }
        }
    }

    // --- 实现搜索方法 ---
    @Override
    public PageInfo<Enterprise> searchEnterprises(SearchEnterpriseRequest request) {
        // 可选：在 Service 层再次校验地区参数
        if (StringUtils.hasText(request.getRegion()) && !ALLOWED_REGIONS.contains(request.getRegion())) {
             // 可以选择抛出异常或清空无效地区条件
             // throw new IllegalArgumentException("搜索地区无效，只允许: " + String.join(", ", ALLOWED_REGIONS));
             request.setRegion(null); // 或者忽略无效条件
             System.out.println("警告：提供的搜索地区无效，已忽略。");
        }

        // 使用 PageHelper 启动分页
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        // 执行查询
        List<Enterprise> list = enterpriseMapper.search(request);
        // 用 PageInfo 包装查询结果
        return new PageInfo<>(list);
    }
}