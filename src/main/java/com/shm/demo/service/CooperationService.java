package com.shm.demo.service;

import com.shm.demo.dto.*; // 确保导入了 DTO
import com.shm.demo.entity.Cooperation;
// import javax.persistence.EntityNotFoundException; // 移除旧的导入
import com.shm.demo.exception.ResourceNotFoundException; // 导入自定义异常

import java.util.List;

public interface CooperationService {

    /**
     * 添加新的合作信息及其人员明细
     * @param request 包含合作主体和人员列表的请求 DTO
     * @return 创建成功的 Cooperation 实体 (包含生成的 ID)
     * @throws IllegalArgumentException 如果输入无效或校验失败 (如时间重叠)
     */
    Cooperation addCooperation(CreateCooperationRequest request) throws IllegalArgumentException;

    /**
     * 修改合作信息及其人员明细
     * @param id 要修改的合作信息的 ID
     * @param request 包含更新后合作主体和人员列表的请求 DTO
     * @return 更新成功的 Cooperation 实体
     * @throws IllegalArgumentException 如果输入无效、记录不存在或校验失败 (如时间重叠)
     */
    Cooperation updateCooperation(UpdateCooperationRequest request) throws IllegalArgumentException;

    /**
     * 分页查询合作列表
     * @param paginationRequest 包含页码和每页数量的请求对象
     * @return 包含合作列表和分页信息的结果对象
     */
    PageResponse<CooperationListItemDTO> listCooperations(PaginationRequest paginationRequest);

    /**
     * 根据条件搜索合作列表 (分页)
     * @param request 包含搜索条件和分页信息的请求对象
     * @return 包含符合条件的合作列表和分页信息的结果对象
     */
    PageResponse<CooperationListItemDTO> searchCooperations(SearchCooperationRequest request); // 新增搜索方法

    /**
     * 根据 ID 获取合作详细信息
     * @param id 合作 ID
     * @return 合作详细信息 DTO
     * @throws ResourceNotFoundException 如果找不到对应的合作信息
     */
    CooperationDetailDTO getCooperationDetails(Long id) throws ResourceNotFoundException; // 修改抛出的异常类型

    void deleteCooperation(Long id) throws ResourceNotFoundException;

    void deleteCooperationsBatch(List<Long> ids) throws IllegalArgumentException;
}