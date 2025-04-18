package com.shm.demo.mapper;

import com.shm.demo.dto.CooperationPersonnelDetailDTO;
import com.shm.demo.entity.CooperationPersonnel;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CooperationPersonnelMapper {

    @Insert("<script>" +
            "INSERT INTO cooperation_personnel(cooperation_id, sending_enterprise_id, personnel_id, cooperation_job_type, " +
            "receiving_enterprise_id, personnel_start_date, personnel_end_date) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.cooperationId}, #{item.sendingEnterpriseId}, #{item.personnelId}, #{item.cooperationJobType}, " +
            "#{item.receivingEnterpriseId}, #{item.personnelStartDate}, #{item.personnelEndDate})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<CooperationPersonnel> list);

    @Delete("DELETE FROM cooperation_personnel WHERE cooperation_id = #{cooperationId}")
    int deleteByCooperationId(@Param("cooperationId") Long cooperationId);

    /**
     * 查找指定人员在指定时间段内是否存在于任何未删除的合作中 (排除指定的合作ID)
     * @param personnelId 人员ID
     * @param startDate   查询的开始日期
     * @param endDate     查询的结束日期
     * @param excludedCooperationId 要排除的合作ID (用于更新场景)
     * @return 存在的合作人员明细列表 (如果为空则表示无重叠)
     */
    @Select("SELECT cp.* FROM cooperation_personnel cp " +
            "JOIN cooperation c ON cp.cooperation_id = c.id " +
            "WHERE cp.personnel_id = #{personnelId} " +
            "AND c.deleted = 0 " +
            "AND cp.cooperation_id != #{excludedCooperationId} " + // 排除当前正在修改的合作
            "AND cp.personnel_end_date >= #{startDate} " + // 现有结束 >= 新开始
            "AND cp.personnel_start_date <= #{endDate}")   // 现有开始 <= 新结束
    List<CooperationPersonnel> findOverlappingPersonnelAssignmentsExcludingCooperation(
            @Param("personnelId") Long personnelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludedCooperationId") Long excludedCooperationId);

    // --- 新增 findOverlappingPersonnelAssignments 方法 ---
    /**
     * 查找指定人员在指定时间段内是否存在于任何未删除的合作中 (用于新增场景)
     * @param personnelId 人员ID
     * @param startDate   查询的开始日期
     * @param endDate     查询的结束日期
     * @return 存在的合作人员明细列表 (如果为空则表示无重叠)
     */
    @Select("SELECT cp.* FROM cooperation_personnel cp " +
            "JOIN cooperation c ON cp.cooperation_id = c.id " +
            "WHERE cp.personnel_id = #{personnelId} " +
            "AND c.deleted = 0 " +
            "AND cp.personnel_end_date >= #{startDate} " + // 现有结束 >= 新开始
            "AND cp.personnel_start_date <= #{endDate}")   // 现有开始 <= 新结束
    List<CooperationPersonnel> findOverlappingPersonnelAssignments(
            @Param("personnelId") Long personnelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 根据合作 ID 查询合作人员详细信息列表
     * @param cooperationId 合作 ID
     * @return 合作人员详细信息 DTO 列表
     */
    @Select("SELECT " +
            "  p.name AS personnelName, " + // 从 personnel 表获取姓名
            "  emp_e.name AS employingEnterpriseName, " + // 从 enterprise 表获取任职企业名称
            "  cp.cooperation_job_type AS cooperationType, " + // 使用正确的字段名 cooperation_job_type
            // --- 移除 cooperation_region ---
            // "  cp.cooperation_region AS cooperationRegion, " + // 此字段在表中不存在，移除
            // --- 结束移除 ---
            "  coop_e.name AS cooperationEnterpriseName, " + // 从 enterprise 表获取合作企业名称 (关联接收企业)
            "  cp.personnel_start_date AS cooperationStartDate, " + // 使用正确的字段名
            "  cp.personnel_end_date AS cooperationEndDate " + // 使用正确的字段名
            "FROM cooperation_personnel cp " +
            "LEFT JOIN personnel p ON cp.personnel_id = p.id " + // 关联 personnel 表
            "LEFT JOIN enterprise emp_e ON p.enterprise_id = emp_e.id " + // 关联 enterprise 表 (任职企业)
            "LEFT JOIN enterprise coop_e ON cp.receiving_enterprise_id = coop_e.id " + // 关联 enterprise 表 (合作企业 - 接收方)
            "WHERE cp.cooperation_id = #{cooperationId}") // 移除了不存在的 cp.deleted 条件
    List<CooperationPersonnelDetailDTO> findDetailByCooperationId(@Param("cooperationId") Long cooperationId);

    // --- 新增批量删除方法 ---
    /**
     * 根据 Cooperation ID 列表批量删除所有关联的人员记录
     * (用于批量删除场景)
     * @param cooperationIds 合作主记录 ID 列表
     * @return 影响的行数
     */
    @Delete("<script>" +
            "DELETE FROM cooperation_personnel WHERE cooperation_id IN " +
            "<foreach item='id' collection='cooperationIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int deleteByCooperationIds(@Param("cooperationIds") List<Long> cooperationIds);
    // --- 结束新增批量删除方法 ---
}