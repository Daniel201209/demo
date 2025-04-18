package com.shm.demo.mapper;

import com.shm.demo.entity.Cooperation;
import com.shm.demo.dto.CooperationListItemDTO; // 引入 DTO
import org.apache.ibatis.annotations.*;
import java.util.List; // 引入 List

@Mapper
public interface CooperationMapper {

    @Insert("INSERT INTO cooperation(cooperation_theme, initiator_region, receiver_region, cooperation_start_date, cooperation_end_date) " +
            "VALUES(#{cooperationTheme}, #{initiatorRegion}, #{receiverRegion}, #{cooperationStartDate}, #{cooperationEndDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cooperation cooperation);

    @Select("SELECT COUNT(*) FROM cooperation WHERE cooperation_theme = #{theme} AND deleted = 0")
    int countByTheme(@Param("theme") String theme);

    @Select("SELECT * FROM cooperation WHERE id = #{id} AND deleted = 0")
    Cooperation findById(@Param("id") Long id);

    @Select("SELECT * FROM cooperation WHERE id = #{id}") // 查询原始记录，无论是否删除
    Cooperation findRawById(@Param("id") Long id);

    @Update("UPDATE cooperation SET " +
            "cooperation_theme = #{cooperationTheme}, " +
            "initiator_region = #{initiatorRegion}, " +
            "receiver_region = #{receiverRegion}, " +
            "cooperation_start_date = #{cooperationStartDate}, " +
            "cooperation_end_date = #{cooperationEndDate}, " +
            "updated_at = CURRENT_TIMESTAMP " + // 通常更新时自动更新 updated_at
            "WHERE id = #{id} AND deleted = 0")
    int update(Cooperation cooperation);

    @Select("SELECT COUNT(*) FROM cooperation WHERE cooperation_theme = #{theme} AND id != #{id} AND deleted = 0")
    int countByThemeAndNotId(@Param("theme") String theme, @Param("id") Long id);

    /**
     * 分页查询合作列表，并统计每个合作的人员数量
     * 按创建时间降序排序
     * @param offset 记录偏移量 (page - 1) * size
     * @param size 每页数量
     * @return 包含人员数量的合作列表项 DTO 列表
     */
    @Select("SELECT " +
            "c.id, c.cooperation_theme, c.initiator_region, c.receiver_region, " +
            "c.cooperation_start_date, c.cooperation_end_date, c.created_at, " +
            "COUNT(DISTINCT cp.id) AS personnel_count " + // 计算非重复人员明细数量
            "FROM cooperation c " +
            "LEFT JOIN cooperation_personnel cp ON c.id = cp.cooperation_id " + // 左连接以包含没有人员的合作
            "WHERE c.deleted = 0 " + // 只查询未删除的
            "GROUP BY c.id " + // 按合作 ID 分组
            "ORDER BY c.created_at DESC " + // 按创建时间降序
            "LIMIT #{size} OFFSET #{offset}")
    List<CooperationListItemDTO> findPaginatedWithCount(@Param("offset") int offset, @Param("size") int size);

    /**
     * 查询未删除的合作总数
     * @return 总记录数
     */
    @Select("SELECT COUNT(*) FROM cooperation WHERE deleted = 0")
    long countTotal();

    // 可能需要的 softDelete 方法
    @Update("UPDATE cooperation SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = #{id} AND deleted = 0")
    int softDeleteById(@Param("id") Long id);
}