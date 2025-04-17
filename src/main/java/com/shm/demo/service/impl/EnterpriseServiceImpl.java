package com.shm.demo.service.impl;

import com.shm.demo.entity.Enterprise;
import com.shm.demo.mapper.EnterpriseMapper;
import com.shm.demo.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects; // 导入 Objects

@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Override
    public boolean isNameUnique(String name) {
        // countByName 内部已改为检查 deleted = 0
        return enterpriseMapper.countByName(name) == 0;
    }

    // 校验逻辑
    private void validateEnterprise(Enterprise enterprise, boolean isUpdate) throws IllegalArgumentException {
        // ... (非空和长度校验不变) ...

        // 唯一性校验
        // 使用新添加的 Mapper 方法 findRawByName
        Enterprise existingByName = enterpriseMapper.findRawByName(enterprise.getName());

        if (existingByName != null && existingByName.getDeleted() == 0) { // 修改为检查 deleted == 0
            if (!isUpdate || !Objects.equals(existingByName.getId(), enterprise.getId())) {
                throw new IllegalArgumentException("企业名称已存在: " + enterprise.getName());
            }
        }
    }

    // 移除临时的 findByNameIgnoringDeleted 方法，因为校验逻辑已直接使用 findRawByName

    @Override
    @Transactional
    public Enterprise addEnterprise(Enterprise enterprise) throws IllegalArgumentException {
        validateEnterprise(enterprise, false);
        enterpriseMapper.insert(enterprise);
        return enterprise;
    }

    // ... getAllEnterprises, getEnterpriseById 不变 ...
    @Override
    public List<Enterprise> getAllEnterprises() {
        return enterpriseMapper.findAll();
    }

    @Override
    public Enterprise getEnterpriseById(Long id) {
        return enterpriseMapper.findById(id);
    }


    @Override
    @Transactional
    public Enterprise updateEnterprise(Enterprise enterprise) throws IllegalArgumentException {
        if (enterprise == null || enterprise.getId() == null) {
            throw new IllegalArgumentException("更新时企业信息和ID不能为空");
        }
        // 确保不会意外更新 deleted 状态
        enterprise.setDeleted(null); // 设为 null，这样 update 语句的 if 判断不会包含它

        validateEnterprise(enterprise, true); // 校验

        int updatedRows = enterpriseMapper.update(enterprise);
        if (updatedRows == 0) {
            // 检查记录是否真的不存在，或者只是已被逻辑删除 (deleted=1)
            Enterprise rawExisting = enterpriseMapper.findRawById(enterprise.getId()); // 需要添加 findRawById Mapper 方法
             if (rawExisting == null) {
                 throw new IllegalArgumentException("未找到要更新的企业，ID: " + enterprise.getId());
             } else if (rawExisting.getDeleted() == 1) {
                 throw new IllegalArgumentException("企业已被删除，无法更新，ID: " + enterprise.getId());
             } else {
                 // 其他情况，可能并发导致
                 throw new IllegalStateException("更新企业时发生未知错误，ID: " + enterprise.getId());
             }
        }
        // 返回更新后的完整信息 (findById 会自动过滤 deleted=1 的)
        return enterpriseMapper.findById(enterprise.getId());
    }

    /**
     * 逻辑删除企业 (调用 softDeleteById 将 deleted 设为 1)
     * @param id 企业 ID
     */
    @Override
    @Transactional
    public void deleteEnterprise(Long id) { // 方法签名和基本逻辑不变
        if (id == null) {
            throw new IllegalArgumentException("删除时企业ID不能为空");
        }
        // softDeleteById 现在会将 deleted 设置为 1
        int deletedRows = enterpriseMapper.softDeleteById(id);
        if (deletedRows == 0) {
            // 检查记录是否真的不存在，或者只是已被逻辑删除 (deleted=1)
            Enterprise rawExisting = enterpriseMapper.findRawById(id); // 需要添加 findRawById Mapper 方法
             if (rawExisting == null) {
                 throw new IllegalArgumentException("未找到要删除的企业，ID: " + id);
             } else if (rawExisting.getDeleted() == 1) {
                 // 已经是删除状态，重复删除影响行数为 0 是正常的，可以不抛异常或只记录日志
                 System.out.println("企业 ID: " + id + " 已被删除。");
             } else {
                 // 其他情况，可能并发导致
                 throw new IllegalStateException("删除企业时发生未知错误，ID: " + id);
             }
        }
    }

    // === 需要在 EnterpriseMapper 中添加以下方法 ===
    // Enterprise findRawById(@Param("id") Long id);
    // @Select("SELECT ... FROM enterprise WHERE id = #{id} LIMIT 1")
    // @ResultMap("BaseResultMap")
}