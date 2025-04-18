package com.shm.demo.mapper;

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

}