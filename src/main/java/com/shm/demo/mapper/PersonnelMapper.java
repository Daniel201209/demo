package com.shm.demo.mapper;

import com.github.pagehelper.Page; // 引入 PageHelper 的 Page
import com.shm.demo.dto.SearchPersonnelRequest; // 引入请求 DTO
import com.shm.demo.entity.Personnel;
import org.apache.ibatis.annotations.*;
// import org.apache.ibatis.type.JdbcType; // 不再需要显式指定 JdbcType

import java.util.List;

@Mapper
public interface PersonnelMapper {

    // --- 移除结果映射定义 ---
    // @Results(...)
    // default void resultMapDefinition(){}

    // --- 查询操作 ---

    /**
     * 根据 ID 查询未删除的人员信息
     */
    // 移除 @ResultMap, 显式指定列
    @Select("SELECT id, name, gender, age, phone, education, start_work_date, enterprise_id, created_at, updated_at, deleted " +
            "FROM personnel WHERE id = #{id} AND deleted = 0")
    Personnel findById(@Param("id") Long id);

    /**
     * 查询所有未删除的人员信息
     * 注意：如果数据量大，应考虑分页查询
     */
    // 移除 @ResultMap, 显式指定列
    @Select("SELECT id, name, gender, age, phone, education, start_work_date, enterprise_id, created_at, updated_at, deleted " +
            "FROM personnel WHERE deleted = 0")
    List<Personnel> findAll();

    /**
     * 根据手机号查询未删除的人员数量 (用于校验唯一性)
     */
    // 此方法返回 int，不需要映射
    @Select("SELECT count(*) FROM personnel WHERE phone = #{phone} AND deleted = 0")
    int countByPhone(@Param("phone") String phone);

    /**
     * 根据手机号查询人员信息（无论是否删除，用于更新时校验）
     */
    // 移除 @ResultMap, 显式指定列
    @Select("SELECT id, name, gender, age, phone, education, start_work_date, enterprise_id, created_at, updated_at, deleted " +
            "FROM personnel WHERE phone = #{phone} LIMIT 1")
    Personnel findRawByPhone(@Param("phone") String phone);

    /**
     * 根据 ID 查询人员信息（无论是否删除，用于检查记录真实状态）
     */
    // 移除 @ResultMap, 显式指定列
    @Select("SELECT id, name, gender, age, phone, education, start_work_date, enterprise_id, created_at, updated_at, deleted " +
            "FROM personnel WHERE id = #{id} LIMIT 1")
    Personnel findRawById(@Param("id") Long id);

    // --- 新增搜索方法 ---
    /**
     * 根据条件搜索人员信息 (分页)
     * @param request 包含搜索条件和分页参数的请求对象
     * @return 分页后的人员列表 (Page 对象)
     */
    @Select("<script>" +
            "SELECT id, name, gender, age, phone, education, start_work_date, enterprise_id, created_at, updated_at, deleted " +
            "FROM personnel " +
            "WHERE deleted = 0 " +
            "<if test='name != null and name != \"\"'>" + // 注意 OGNL 表达式中字符串比较用 .equals("") 或 != ""
            "  AND name LIKE CONCAT('%', #{name}, '%') " +
            "</if>" +
            "<if test='gender != null'>" +
            "  AND gender = #{gender} " +
            "</if>" +
            "<if test='enterpriseId != null'>" +
            "  AND enterprise_id = #{enterpriseId} " +
            "</if>" +
            // 可以添加排序逻辑, 例如 ORDER BY created_at DESC
            "</script>")
    Page<Personnel> search(SearchPersonnelRequest request); // 返回 Page 对象以支持 PageHelper 分页


    // --- 插入操作 ---
    // ... insert 方法不变 ...
    @Insert("INSERT INTO personnel (name, gender, age, phone, education, start_work_date, enterprise_id) " +
            "VALUES (#{name}, #{gender}, #{age}, #{phone}, #{education}, #{startWorkDate}, #{enterpriseId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Personnel personnel);


    // --- 更新操作 ---
    // ... update 方法不变 ...
    @Update("<script>" +
            "UPDATE personnel " +
            "<set>" +
            " <if test='name != null'>name = #{name},</if>" +
            " <if test='gender != null'>gender = #{gender},</if>" +
            " <if test='age != null'>age = #{age},</if>" +
            " <if test='phone != null'>phone = #{phone},</if>" +
            " <if test='education != null'>education = #{education},</if>" +
            " <if test='startWorkDate != null'>start_work_date = #{startWorkDate},</if>" +
            " <if test='enterpriseId != null'>enterprise_id = #{enterpriseId},</if>" +
            // updated_at 由数据库自动更新，不需要在此设置
            "</set>" +
            "WHERE id = #{id} AND deleted = 0" + // 只能更新未删除的记录
            "</script>")
    int update(Personnel personnel);


    // --- 删除操作 ---
    // ... softDeleteById 方法不变 ...
    @Update("UPDATE personnel SET deleted = 1 WHERE id = #{id} AND deleted = 0")
    int softDeleteById(@Param("id") Long id);

}