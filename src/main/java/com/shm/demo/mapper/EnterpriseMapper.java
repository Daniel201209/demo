package com.shm.demo.mapper;

import com.shm.demo.entity.Enterprise;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface EnterpriseMapper {

    /**
     * 根据名称查询未删除的企业数量
     */
    @Select("SELECT count(*) FROM enterprise WHERE name = #{name} AND deleted = 0") // 修改为 deleted = 0
    int countByName(@Param("name") String name);

    /**
     * 新增企业 (deleted 字段使用数据库默认值 0)
     */
    // ... insert 语句不变 ...
    @Insert("INSERT INTO enterprise (name, cooperation_type, enterprise_type, region) " +
            "VALUES (#{name}, #{cooperationType}, #{enterpriseType}, #{region})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Enterprise enterprise);


    /**
     * 根据 ID 查询未删除的企业
     */
    // 修改 @Results 中的 deleted 映射类型
    @Results(id = "BaseResultMap", value = {
            // ... 其他映射 ...
            @Result(column = "region", property = "region"),
            @Result(column = "deleted", property = "deleted", jdbcType = JdbcType.TINYINT) // 修改映射类型
    })
    @Select("SELECT id, name, cooperation_type, enterprise_type, region, deleted FROM enterprise WHERE id = #{id} AND deleted = 0") // 修改为 deleted = 0
    Enterprise findById(@Param("id") Long id);

    /**
     * 查询所有未删除的企业
     */
    @Select("SELECT id, name, cooperation_type, enterprise_type, region, deleted FROM enterprise WHERE deleted = 0") // 修改为 deleted = 0
    @ResultMap("BaseResultMap")
    List<Enterprise> findAll();

    /**
     * 更新企业信息
     */
    @Update("<script>" +
            "UPDATE enterprise " +
            "<set>" +
            // ... 其他字段更新 ...
            "<if test='region != null'>region = #{region},</if>" +
            "</set>" +
            "WHERE id = #{id} AND deleted = 0" + // 修改为 deleted = 0
            "</script>")
    int update(Enterprise enterprise);

    /**
     * 根据 ID 逻辑删除企业 (设置为 1)
     */
    @Update("UPDATE enterprise SET deleted = 1 WHERE id = #{id} AND deleted = 0") // 修改为 SET deleted = 1 和 WHERE deleted = 0
    int softDeleteById(@Param("id") Long id);

    // ... hardDeleteById (可选) ...

    // ... resultMapDefinition 保持不变 ...
    default void resultMapDefinition(){}

    // !! 需要添加这个方法用于 Service 中的唯一性校验 !!
    /**
     * 根据名称查询企业（无论是否删除，主要用于更新时的唯一性校验）
     * @param name 企业名称
     * @return 企业信息，可能为 null
     */
    @Select("SELECT id, name, cooperation_type, enterprise_type, region, deleted FROM enterprise WHERE name = #{name} LIMIT 1")
    @ResultMap("BaseResultMap") // 复用结果映射
    Enterprise findRawByName(@Param("name") String name);

    /**
     * 根据 ID 查询企业（无论是否删除，用于检查记录真实状态）
     * @param id 企业 ID
     * @return 企业信息，可能为 null
     */
    @Select("SELECT id, name, cooperation_type, enterprise_type, region, deleted FROM enterprise WHERE id = #{id} LIMIT 1")
    @ResultMap("BaseResultMap") // 复用结果映射
    Enterprise findRawById(@Param("id") Long id); // 新增的方法

}