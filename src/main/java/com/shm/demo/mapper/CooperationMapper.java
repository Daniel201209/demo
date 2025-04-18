package com.shm.demo.mapper;

import com.shm.demo.dto.CooperationListItemDTO;
import com.shm.demo.dto.SearchCooperationRequest;
import com.shm.demo.entity.Cooperation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CooperationMapper {

    @Insert("INSERT INTO cooperation (cooperation_theme, initiator_region, receiver_region, cooperation_start_date, cooperation_end_date, deleted) " +
            "VALUES (#{cooperationTheme}, #{initiatorRegion}, #{receiverRegion}, #{cooperationStartDate}, #{cooperationEndDate}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cooperation cooperation);

    @Select("SELECT * FROM cooperation WHERE id = #{id} AND deleted = 0")
    Cooperation findById(@Param("id") Long id);

    @Select("SELECT * FROM cooperation WHERE id = #{id}") // 用于检查原始状态，包括已删除的
    Cooperation findRawById(@Param("id") Long id);

    /**
     * 根据 ID 逻辑删除单个合作信息 (更新 deleted 标志)
     * @param id 要删除的合作 ID
     * @return 影响的行数 (通常为 1 或 0)
     */
    @Update("UPDATE cooperation SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = #{id} AND deleted = 0")
    int markAsDeleted(@Param("id") Long id);

    /**
     * 根据 ID 列表批量逻辑删除合作信息 (更新 deleted 标志)
     * @param ids 要删除的合作 ID 列表
     * @return 影响的行数
     */
    @Update("<script>" +
            "UPDATE cooperation SET deleted = 1, updated_at = CURRENT_TIMESTAMP " +
            "WHERE deleted = 0 AND id IN " + // 只更新未删除的记录
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchMarkAsDeleted(@Param("ids") List<Long> ids);

    @Update("UPDATE cooperation SET " +
            "cooperation_theme = #{cooperationTheme}, " +
            "initiator_region = #{initiatorRegion}, " +
            "receiver_region = #{receiverRegion}, " +
            "cooperation_start_date = #{cooperationStartDate}, " +
            "cooperation_end_date = #{cooperationEndDate}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = #{id} AND deleted = 0")
    int update(Cooperation cooperation);

    @Select("SELECT COUNT(*) FROM cooperation WHERE cooperation_theme = #{theme} AND deleted = 0")
    int countByTheme(@Param("theme") String theme);

    @Select("SELECT COUNT(*) FROM cooperation WHERE cooperation_theme = #{theme} AND id != #{id} AND deleted = 0")
    int countByThemeAndNotId(@Param("theme") String theme, @Param("id") Long id);

    @Update("UPDATE cooperation SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = #{id} AND deleted = 0")
    int softDeleteById(@Param("id") Long id);

    @Select("SELECT " +
            "c.id, c.cooperation_theme, c.initiator_region, c.receiver_region, " +
            "c.cooperation_start_date, c.cooperation_end_date, c.created_at, " +
            "COUNT(DISTINCT cp.id) AS personnel_count " +
            "FROM cooperation c " +
            "LEFT JOIN cooperation_personnel cp ON c.id = cp.cooperation_id " +
            "WHERE c.deleted = 0 " +
            "GROUP BY c.id " +
            "ORDER BY c.created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}")
    List<CooperationListItemDTO> findPaginatedWithCount(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM cooperation WHERE deleted = 0")
    long countTotal();

    // --- 新增搜索方法 ---

   /**
     * 根据搜索条件分页查询合作列表，并统计人员数量 (使用 @Select 和 <script>)
     *
     * @param request 搜索条件 DTO
     * @param offset  记录偏移量
     * @param size    每页数量
     * @return 符合条件的合作列表项 DTO 列表
     */
    @Select("<script>" + // 使用 <script> 标签开启动态 SQL
            "SELECT " +
            "  c.id, c.cooperation_theme, c.initiator_region, c.receiver_region, " +
            "  c.cooperation_start_date, c.cooperation_end_date, c.created_at, " +
            "  COUNT(DISTINCT cp.id) AS personnel_count " +
            "FROM cooperation c " +
            "LEFT JOIN cooperation_personnel cp ON c.id = cp.cooperation_id " +
            "WHERE c.deleted = 0 " +
            // 动态添加 cooperation_theme 条件
            "<if test='request.cooperationTheme != null and request.cooperationTheme != \"\"'>" +
            "  AND c.cooperation_theme LIKE CONCAT('%', #{request.cooperationTheme}, '%') " +
            "</if>" +
            // 动态添加 initiator_region 条件
            "<if test='request.initiatorRegion != null and request.initiatorRegion != \"\"'>" +
            "  AND c.initiator_region = #{request.initiatorRegion} " +
            "</if>" +
            // 动态添加 receiver_region 条件
            "<if test='request.receiverRegion != null and request.receiverRegion != \"\"'>" +
            "  AND c.receiver_region = #{request.receiverRegion} " +
            "</if>" +
            "GROUP BY c.id " + // 添加 GROUP BY
            "ORDER BY c.created_at DESC " + // 添加 ORDER BY
            "LIMIT #{size} OFFSET #{offset}" + // 添加 LIMIT 和 OFFSET
            "</script>") // 结束 <script> 标签
    List<CooperationListItemDTO> searchPaginatedWithCount(
            @Param("request") SearchCooperationRequest request, // 将整个 DTO 作为参数传递
            @Param("offset") int offset,
            @Param("size") int size);

    /**
     * 根据搜索条件查询合作总数 (使用 @Select 和 <script>)
     *
     * @param request 搜索条件 DTO
     * @return 符合条件的总记录数
     */
    @Select("<script>" + // 使用 <script> 标签开启动态 SQL
            "SELECT COUNT(*) " +
            "FROM cooperation c " +
            "WHERE c.deleted = 0 " +
            // 动态添加 cooperation_theme 条件
            "<if test='request.cooperationTheme != null and request.cooperationTheme != \"\"'>" + // 注意 test 条件中的引号和转义
            "  AND c.cooperation_theme LIKE CONCAT('%', #{request.cooperationTheme}, '%') " +
            "</if>" +
            // 动态添加 initiator_region 条件
            "<if test='request.initiatorRegion != null and request.initiatorRegion != \"\"'>" +
            "  AND c.initiator_region = #{request.initiatorRegion} " +
            "</if>" +
            // 动态添加 receiver_region 条件
            "<if test='request.receiverRegion != null and request.receiverRegion != \"\"'>" +
            "  AND c.receiver_region = #{request.receiverRegion} " +
            "</if>" +
            "</script>")
    // 结束 <script> 标签
    long countTotalSearch(@Param("request") SearchCooperationRequest request); // 将整个 DTO 作为参数传递
}