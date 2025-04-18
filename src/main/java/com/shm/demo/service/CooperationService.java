package com.shm.demo.service;

import com.shm.demo.dto.*; // 引入 DTO 包
import com.shm.demo.entity.Cooperation;

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
    PageResponse<CooperationListItemDTO> listCooperations(PaginationRequest paginationRequest); // 新增方法

    // 未来可能添加的方法: getCooperationById, searchCooperations, deleteCooperation 等
}