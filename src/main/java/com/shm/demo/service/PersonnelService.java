package com.shm.demo.service;

import com.github.pagehelper.PageInfo; // 引入 PageInfo
import com.shm.demo.dto.SearchPersonnelRequest; // 引入请求 DTO
import com.shm.demo.entity.Personnel;
import java.util.List;

public interface PersonnelService {

    /**
     * 添加新的人员信息
     * @param personnel 人员信息
     * @return 添加后的人员信息 (包含生成的 ID)
     * @throws IllegalArgumentException 如果数据校验失败 (例如手机号已存在)
     */
    Personnel addPersonnel(Personnel personnel) throws IllegalArgumentException;

    /**
     * 根据 ID 获取人员信息
     * @param id 人员 ID
     * @return 人员信息，如果未找到或已删除则返回 null
     */
    Personnel getPersonnelById(Long id);

    /**
     * 获取所有未删除的人员列表
     * @return 人员列表
     */
    List<Personnel> getAllPersonnel(); // 实际应用中建议分页

    /**
     * 更新人员信息
     * @param personnel 包含 ID 和待更新字段的人员信息
     * @return 更新后的人员信息
     * @throws IllegalArgumentException 如果数据校验失败或记录不存在/已删除
     */
    Personnel updatePersonnel(Personnel personnel) throws IllegalArgumentException;

    /**
     * 根据 ID 逻辑删除人员信息
     * @param id 人员 ID
     * @throws IllegalArgumentException 如果 ID 为 null 或记录不存在/已被删除
     */
    void deletePersonnel(Long id) throws IllegalArgumentException;

    /**
     * 根据条件搜索人员信息 (分页)
     * @param request 包含搜索条件和分页参数的请求对象
     * @return 包含分页信息的人员列表 (PageInfo 对象)
     */
    PageInfo<Personnel> searchPersonnel(SearchPersonnelRequest request); // 返回 PageInfo

}