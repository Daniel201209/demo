package com.shm.demo.controller;

import com.github.pagehelper.PageInfo; // 引入 PageInfo
import com.shm.demo.dto.CreatePersonnelRequest;
import com.shm.demo.dto.PersonnelResponse;
import com.shm.demo.dto.SearchPersonnelRequest; // 引入请求 DTO
import com.shm.demo.dto.UpdatePersonnelRequest;
import com.shm.demo.entity.Personnel;
import com.shm.demo.service.PersonnelService;
import javax.validation.Valid; // 使用 jakarta.validation
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personnel") // API 基础路径
@Validated // 开启方法级别的校验 (如果需要的话)
public class PersonnelController {

    @Autowired
    private PersonnelService personnelService;

    // 创建人员
    @PostMapping("/add") // 保持和 EnterpriseController 风格一致
    public ResponseEntity<?> createPersonnel(@Valid @RequestBody CreatePersonnelRequest request) {
        try {
            Personnel personnel = new Personnel();
            BeanUtils.copyProperties(request, personnel); // DTO -> Entity
            Personnel createdPersonnel = personnelService.addPersonnel(personnel);
            return ResponseEntity.status(HttpStatus.CREATED).body(PersonnelResponse.fromEntity(createdPersonnel)); // Entity -> DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 建议记录日志 log.error("Error creating personnel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("创建人员时发生内部错误");
        }
    }

    // 根据ID获取人员信息
    @PostMapping("/getId") // 保持风格一致，使用 POST + RequestBody 传 ID
    public ResponseEntity<?> getPersonnelById(@RequestBody Long id) {
         if (id == null) {
             return ResponseEntity.badRequest().body("ID 不能为空");
         }
        Personnel personnel = personnelService.getPersonnelById(id);
        if (personnel != null) {
            return ResponseEntity.ok(PersonnelResponse.fromEntity(personnel));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到ID为 " + id + " 的人员信息或已被删除");
        }
    }

    // 获取所有人员列表
    @GetMapping("/getAllPersonnel") // 获取列表通常用 GET
    public ResponseEntity<?> getAllPersonnel() {
        try {
            List<Personnel> personnelList = personnelService.getAllPersonnel();
            List<PersonnelResponse> responseList = personnelList.stream()
                    .map(PersonnelResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
             // log.error("Error getting all personnel", e);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取人员列表时发生内部错误");
        }
    }

    // 更新人员信息
    @PostMapping("/updateDataId") // 保持风格一致
    public ResponseEntity<?> updatePersonnel(@Valid @RequestBody UpdatePersonnelRequest request) {
        try {
            Personnel personnelToUpdate = new Personnel();
            BeanUtils.copyProperties(request, personnelToUpdate); // DTO -> Entity
            Personnel updatedPersonnel = personnelService.updatePersonnel(personnelToUpdate);
            return ResponseEntity.ok(PersonnelResponse.fromEntity(updatedPersonnel)); // Entity -> DTO
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 包括 Not Found 或 Validation 错误
        } catch (Exception e) {
            // log.error("Error updating personnel with id {}", request.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新人员信息时发生内部错误");
        }
    }

    // 删除人员（逻辑删除）
    @PostMapping("/deleteId") // 保持风格一致
    public ResponseEntity<?> deletePersonnel(@RequestBody Long id) {
         if (id == null) {
             return ResponseEntity.badRequest().body("ID 不能为空");
         }
        try {
            personnelService.deletePersonnel(id);
            // 参考 EnterpriseController，删除成功返回自定义消息和特定状态码
            // return ResponseEntity.noContent().build(); // 标准 RESTful 返回 204
             return ResponseEntity.status(HttpStatus.OK).body("删除成功"); // 或者根据你的 API 规范返回
        } catch (IllegalArgumentException e) {
            // Service 层会处理找不到或已删除的情况
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // log.error("Error deleting personnel with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除人员时发生内部错误");
        }
    }

    // --- 新增搜索接口 ---
    /**
     * 根据条件搜索人员信息 (分页)
     * @param request 包含搜索条件和分页参数的请求对象
     * @return 分页后的人员信息列表 (PageInfo<PersonnelResponse>)
     */
    @PostMapping("/search") // 使用 POST 接收包含多个条件的请求体
    public ResponseEntity<?> searchPersonnel(@RequestBody SearchPersonnelRequest request) {
        try {
            // 调用 Service 层进行搜索和分页
            PageInfo<Personnel> personnelPageInfo = personnelService.searchPersonnel(request);

            // 将 Service 返回的 PageInfo<Personnel> 转换为 PageInfo<PersonnelResponse>
            // 1. 获取分页数据列表
            List<PersonnelResponse> responseList = personnelPageInfo.getList().stream()
                    .map(PersonnelResponse::fromEntity) // 使用 DTO 的转换方法
                    .collect(Collectors.toList());

            // 2. 创建一个新的 PageInfo 用于返回给前端，保留分页信息，但替换列表内容
            PageInfo<PersonnelResponse> responsePageInfo = new PageInfo<>();
            BeanUtils.copyProperties(personnelPageInfo, responsePageInfo, "list"); // 复制分页信息，但不复制 list
            responsePageInfo.setList(responseList); // 设置转换后的 DTO 列表

            return ResponseEntity.ok(responsePageInfo);
        } catch (Exception e) {
            // log.error("Error searching personnel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("搜索人员时发生内部错误");
        }
    }
}