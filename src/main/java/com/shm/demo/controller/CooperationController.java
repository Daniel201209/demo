package com.shm.demo.controller;

import com.shm.demo.dto.*; // 引入 DTO 包
// --- 新增导入 ---
import com.shm.demo.exception.ResourceNotFoundException; // 导入自定义异常
import java.util.List; // 导入 List
// --- 结束新增导入 ---
import com.shm.demo.entity.Cooperation;
import com.shm.demo.service.CooperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
// 移除未使用的 NotNull 导入 (如果确实未使用)
// import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/cooperations")
@Validated
public class CooperationController {

    @Autowired
    private CooperationService cooperationService;

    /**
     * 添加合作信息接口
     * @param request 包含合作主体和人员列表的请求 DTO
     * @return 成功时返回创建的合作信息 (HTTP 201)，失败时返回错误信息 (HTTP 400 或 500)
     */
    @PostMapping("/add")
    public ResponseEntity<?> addCooperation(@Valid @RequestBody CreateCooperationRequest request) {
        try {
            Cooperation createdCooperation = cooperationService.addCooperation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCooperation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // log.error("Error adding cooperation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("添加合作信息时发生内部错误");
        }
    }

    @PostMapping("/updateCooperation")
    public ResponseEntity<?> updateCooperation(@Valid @RequestBody UpdateCooperationRequest request) {
        try {
            Cooperation updatedCooperation = cooperationService.updateCooperation(request);
            return ResponseEntity.ok(updatedCooperation);
        } catch (IllegalArgumentException e) {
             if (e.getMessage().contains("未找到") || e.getMessage().contains("已被删除")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
             }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // log.error("Error updating cooperation with id: {}", request.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("修改合作信息时发生内部错误");
        }
    }


    // --- 新增获取合作列表接口 ---
    /**
     * 获取合作信息列表 (分页)
     * @param paginationRequest 包含页码 (page) 和每页数量 (size) 的查询参数
     * @return 分页后的合作信息列表
     */
    @GetMapping("/list") // 使用 GET 请求获取列表
    public ResponseEntity<?> listCooperations(@Valid PaginationRequest paginationRequest) { // 接收分页参数 DTO
        try {
            PageResponse<CooperationListItemDTO> pageResponse = cooperationService.listCooperations(paginationRequest);
            return ResponseEntity.ok(pageResponse); // 返回 200 OK 和分页结果
        } catch (Exception e) {
            // log.error("Error listing cooperations", e); // 建议记录日志
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取合作列表时发生内部错误");
        }
    }


    // --- 新增搜索合作信息接口 ---
    /**
     * 根据条件搜索合作信息列表 (分页)
     * @param request 包含搜索条件 (cooperationTheme, initiatorRegion, receiverRegion) 和分页参数 (page, size) 的请求体
     * @return 分页后的符合条件的合作信息列表
     */
    @PostMapping("/search") // 使用 POST 请求，将搜索条件放在请求体中
    public ResponseEntity<?> searchCooperations(@Valid @RequestBody SearchCooperationRequest request) { // 接收搜索 DTO
        try {
            PageResponse<CooperationListItemDTO> pageResponse = cooperationService.searchCooperations(request);
            return ResponseEntity.ok(pageResponse); // 返回 200 OK 和分页结果
        } catch (Exception e) {
            // log.error("Error searching cooperations", e); // 建议记录日志
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("搜索合作列表时发生内部错误");
        }
    }


    // --- 新增获取合作详情接口 ---
    /**
     * 根据 ID 获取合作详细信息
     * @param id 合作信息的 ID
     * @return 成功时返回合作详细信息 (HTTP 200)，未找到时返回 (HTTP 404)，失败时返回错误信息 (HTTP 500)
     */
    @PostMapping("/getCooperationDetails") // 使用 GET 请求，并通过路径变量传递 ID
    public ResponseEntity<?> getCooperationDetails(@RequestBody Long id) { // 使用 @PathVariable 获取 ID
        try {
            // 调用 Service 层获取详情的方法 (假设返回 CooperationDetailDTO)
            // 您需要确保 CooperationService 中有 getCooperationDetails 方法
            CooperationDetailDTO cooperationDetails = cooperationService.getCooperationDetails(id);
            return ResponseEntity.ok(cooperationDetails); // 返回 200 OK 和详情 DTO
        } catch (ResourceNotFoundException e) { // 捕获 Service 层抛出的未找到异常 (或自定义异常)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到指定的合作信息: " + id); // 返回 404 Not Found
        } catch (Exception e) {
            // log.error("Error getting cooperation details for id: {}", id, e); // 建议记录日志
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取合作详情时发生内部错误");
        }
    }
    // --- 结束新增获取合作详情接口 ---


    // --- 新增单个删除合作信息接口 ---
    /**
     * 根据 ID 逻辑删除单个合作信息
     * @param id 要删除的合作信息的 ID
     * @return 成功时返回 No Content (HTTP 204)，未找到时返回 (HTTP 404)，失败时返回错误信息 (HTTP 500)
     */
    @PostMapping("/deleteCooperation") // 使用 DELETE 请求，并通过路径变量传递 ID
    public ResponseEntity<?> deleteCooperation(@RequestBody Long id) {
        try {
            cooperationService.deleteCooperation(id); // 调用 Service 层执行删除
            return ResponseEntity.noContent().build(); // 返回 204 No Content 表示成功
        } catch (ResourceNotFoundException e) { // 捕获 Service 层抛出的未找到异常
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 返回 404 Not Found
        } catch (Exception e) {
            // log.error("Error deleting cooperation with id: {}", id, e); // 建议记录日志
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除合作信息时发生内部错误");
        }
    }
    // --- 结束新增单个删除合作信息接口 ---


    // --- 新增批量删除合作信息接口 ---
    /**
     * 根据 ID 列表批量逻辑删除合作信息
     * @param request 包含要删除的 ID 列表的请求体 (需要创建 BatchDeleteRequest DTO)
     * @return 成功时返回 No Content (HTTP 204)，失败时返回错误信息 (HTTP 400 或 500)
     */
    @PostMapping("/batch") // 使用 DELETE 请求，路径为 /batch
    public ResponseEntity<?> deleteCooperationsBatch(@Valid @RequestBody BatchDeleteRequest request) {
        try {
            List<Long> ids = request.getIds();
            // Service 层通常也会校验，但 Controller 层可以做初步校验
            if (ids == null || ids.isEmpty()) {
                 return ResponseEntity.badRequest().body("ID列表不能为空");
            }
            cooperationService.deleteCooperationsBatch(ids); // 调用 Service 层执行批量删除
            return ResponseEntity.noContent().build(); // 返回 204 No Content 表示成功
        } catch (IllegalArgumentException e) { // 捕获 Service 层可能抛出的校验异常
             return ResponseEntity.badRequest().body(e.getMessage()); // 返回 400 Bad Request
        } catch (Exception e) {
            // log.error("Error batch deleting cooperations with ids: {}", request.getIds(), e); // 建议记录日志
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("批量删除合作信息时发生内部错误");
        }
    }
    // --- 结束新增批量删除合作信息接口 ---


    // --- 未来可能添加的接口 ---
    // DELETE /api/cooperations/{id} - 逻辑删除合作信息
}