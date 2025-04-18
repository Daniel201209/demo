package com.shm.demo.controller;

import com.shm.demo.dto.*; // 引入 DTO 包
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


    // --- 未来可能添加的接口 ---
    // GET /api/cooperations/{id} - 获取合作详情
    // DELETE /api/cooperations/{id} - 逻辑删除合作信息
}