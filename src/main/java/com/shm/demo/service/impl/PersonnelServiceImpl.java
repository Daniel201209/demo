package com.shm.demo.service.impl;

import com.github.pagehelper.PageHelper; // 引入 PageHelper
import com.github.pagehelper.PageInfo;   // 引入 PageInfo
import com.shm.demo.dto.SearchPersonnelRequest; // 引入请求 DTO
import com.shm.demo.entity.Personnel;
import com.shm.demo.mapper.PersonnelMapper;
import com.shm.demo.service.PersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class PersonnelServiceImpl implements PersonnelService {

    @Autowired
    private PersonnelMapper personnelMapper;

    // 校验逻辑 (可根据需要扩展)
    private void validatePersonnel(Personnel personnel, boolean isUpdate) throws IllegalArgumentException {
        if (personnel == null) {
            throw new IllegalArgumentException("人员信息不能为空");
        }
        if (!StringUtils.hasText(personnel.getName()) || personnel.getName().length() > 10) {
            throw new IllegalArgumentException("姓名不能为空且长度不能超过10");
        }
        if (personnel.getGender() == null || (personnel.getGender() != 1 && personnel.getGender() != 2)) {
            throw new IllegalArgumentException("性别值无效");
        }
        if (personnel.getAge() == null || personnel.getAge() <= 0 || personnel.getAge() > 999) {
            throw new IllegalArgumentException("年龄必须是1到999之间的正整数");
        }
        if (!StringUtils.hasText(personnel.getPhone()) || !personnel.getPhone().matches("^\\d{11}$")) {
            throw new IllegalArgumentException("手机号必须是11位数字");
        }
        if (personnel.getEducation() == null) { // 可以添加更具体的学历枚举校验
            throw new IllegalArgumentException("学历不能为空");
        }
        if (personnel.getStartWorkDate() == null) {
            throw new IllegalArgumentException("参加工作时间不能为空");
        }
        if (personnel.getEnterpriseId() == null) {
            throw new IllegalArgumentException("任职企业ID不能为空");
        }

        // 手机号唯一性校验
        Personnel existingByPhone = personnelMapper.findRawByPhone(personnel.getPhone());
        if (existingByPhone != null && existingByPhone.getDeleted() == 0) { // 检查未删除的记录
            if (!isUpdate || !Objects.equals(existingByPhone.getId(), personnel.getId())) {
                throw new IllegalArgumentException("手机号已存在: " + personnel.getPhone());
            }
        }
        // 可选：校验 enterprise_id 是否存在于 enterprise 表中
        // if (enterpriseMapper.findRawById(personnel.getEnterpriseId()) == null) {
        //     throw new IllegalArgumentException("指定的企业ID不存在: " + personnel.getEnterpriseId());
        // }
    }

    @Override
    @Transactional
    public Personnel addPersonnel(Personnel personnel) throws IllegalArgumentException {
        validatePersonnel(personnel, false); // 新增校验
        personnelMapper.insert(personnel);
        // insert 后 personnel 对象会包含 ID
        return personnel;
    }

    @Override
    public Personnel getPersonnelById(Long id) {
        if (id == null) return null;
        return personnelMapper.findById(id); // findById 内部已处理 deleted = 0
    }

    @Override
    public List<Personnel> getAllPersonnel() {
        return personnelMapper.findAll(); // findAll 内部已处理 deleted = 0
    }

    @Override
    @Transactional
    public Personnel updatePersonnel(Personnel personnel) throws IllegalArgumentException {
        if (personnel == null || personnel.getId() == null) {
            throw new IllegalArgumentException("更新时人员信息和ID不能为空");
        }
        // 确保不意外更新 deleted 状态
        personnel.setDeleted(null); // 设为 null，MyBatis update 语句的 if 判断会忽略它

        validatePersonnel(personnel, true); // 更新校验

        int updatedRows = personnelMapper.update(personnel);
        if (updatedRows == 0) {
            // 检查记录是否真的不存在，或者只是已被逻辑删除
            Personnel rawExisting = personnelMapper.findRawById(personnel.getId());
            if (rawExisting == null) {
                throw new IllegalArgumentException("未找到要更新的人员信息，ID: " + personnel.getId());
            } else if (rawExisting.getDeleted() == 1) {
                throw new IllegalArgumentException("人员信息已被删除，无法更新，ID: " + personnel.getId());
            } else {
                // 可能并发或其他原因导致未更新
                throw new IllegalStateException("更新人员信息时发生未知错误，ID: " + personnel.getId());
            }
        }
        // 返回更新后的完整信息 (findById 会自动过滤 deleted=1 的)
        return personnelMapper.findById(personnel.getId());
    }

    @Override
    @Transactional
    public void deletePersonnel(Long id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("删除时人员ID不能为空");
        }
        int deletedRows = personnelMapper.softDeleteById(id);
        if (deletedRows == 0) {
            // 检查记录是否真的不存在，或者只是已被逻辑删除
            Personnel rawExisting = personnelMapper.findRawById(id);
            if (rawExisting == null) {
                throw new IllegalArgumentException("未找到要删除的人员信息，ID: " + id);
            } else if (rawExisting.getDeleted() == 1) {
                // 已经是删除状态，重复删除是幂等的，可以不抛异常或只记录日志
                System.out.println("人员信息 ID: " + id + " 已被删除。");
            } else {
                throw new IllegalStateException("删除人员信息时发生未知错误，ID: " + id);
            }
        }
    }

    // --- 实现搜索方法 ---
    @Override
    public PageInfo<Personnel> searchPersonnel(SearchPersonnelRequest request) {
        // 使用 PageHelper 启动分页
        // 参数1: pageNum, 第几页
        // 参数2: pageSize, 每页显示条数
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        // 执行查询，PageHelper 会自动拦截这个查询并追加分页逻辑 (例如 LIMIT)
        // 注意：search 方法需要返回 Page<T> 类型，PageHelper 才能正确处理分页信息
        List<Personnel> list = personnelMapper.search(request); // 直接使用 List 接收也可以，PageInfo 会处理
        // 用 PageInfo 包装查询结果，PageInfo 会包含总记录数、总页数、当前页数据等信息
        return new PageInfo<>(list);
    }
}