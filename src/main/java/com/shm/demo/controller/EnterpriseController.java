package com.shm.demo.controller;

import com.shm.demo.entity.Enterprise;
import com.shm.demo.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/enterprises")
@Validated
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    // ... EnterpriseRequest DTO ...

    // ... POST /api/enterprises (createEnterprise) ...
    @PostMapping("/add")
    public ResponseEntity<?> createEnterprise(@Valid @RequestBody EnterpriseRequest request) {
        // ... (代码不变) ...
        try {
            Enterprise enterprise = new Enterprise();
            enterprise.setName(request.getName());
            enterprise.setCooperationType(request.getCooperationType());
            enterprise.setEnterpriseType(request.getEnterpriseType());
            enterprise.setRegion(request.getRegion());

            Enterprise createdEnterprise = enterpriseService.addEnterprise(enterprise);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEnterprise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("创建企业时发生内部错误");
        }
    }


    // ... GET /api/enterprises (getAllEnterprises) ...
    @GetMapping("/getAllEnterprises")
    public ResponseEntity<List<Enterprise>> getAllEnterprises() {
        // ... (代码不变) ...
        List<Enterprise> enterprises = enterpriseService.getAllEnterprises();
        return ResponseEntity.ok(enterprises);
    }

    // ... GET /api/enterprises/{id} (getEnterpriseById) ...
     @PostMapping("/getId")
    public ResponseEntity<?> getEnterpriseById(@RequestBody Long id) {
        // ... (代码不变) ...
        Enterprise enterprise = enterpriseService.getEnterpriseById(id);
        if (enterprise != null) {
            return ResponseEntity.ok(enterprise);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到ID为 " + id + " 的企业");
        }
    }


    // ... PUT /api/enterprises/{id} (updateEnterprise) ...
    @PostMapping("/updataDataId")
    public ResponseEntity<?> updateEnterprise(@Valid @RequestBody EnterpriseRequest request) {
         // ... (代码不变) ...
         try {
            // 从 request 对象中获取 ID
            Long id = request.getId();
            // 可以在这里加一个额外的 null 判断，虽然 @NotNull 应该已经处理了
            if (id == null) {
                 return ResponseEntity.badRequest().body("请求体中必须包含有效的ID");
            }

            // 使用从 request 获取的 id 查询
            Enterprise existing = enterpriseService.getEnterpriseById(id);
            if (existing == null) {
                 // 注意：这里的错误消息可能需要调整，因为 ID 是从请求体来的
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到ID为 " + id + " 的企业或已被删除");
            }

            // 创建要更新的对象
            Enterprise enterpriseToUpdate = new Enterprise();
            // 设置从 request 获取的 ID 和其他属性
            enterpriseToUpdate.setId(id);
            enterpriseToUpdate.setName(request.getName());
            enterpriseToUpdate.setCooperationType(request.getCooperationType());
            enterpriseToUpdate.setEnterpriseType(request.getEnterpriseType());
            enterpriseToUpdate.setRegion(request.getRegion());

            // 调用 Service 层执行更新
            Enterprise updatedEnterprise = enterpriseService.updateEnterprise(enterpriseToUpdate);
            return ResponseEntity.ok(updatedEnterprise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新企业时发生内部错误");
        }
    }
    


    // DELETE 请求现在执行逻辑删除
    @PostMapping("/deleteId")
    public ResponseEntity<?> deleteEnterprise(@RequestBody Long id) {
        try {
            enterpriseService.deleteEnterprise(id); // 调用的是逻辑删除的 Service 方法
            return ResponseEntity.status(HttpStatus.CONTINUE).body("删除成功"); // HTTP 204 No Content
        } catch (IllegalArgumentException e) {
             // 如果 service 层在找不到或已被删除时抛出异常
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除企业时发生内部错误");
        }
    }

    // --- DTO for Request Body ---
    @lombok.Data
    static class EnterpriseRequest {
        @NotNull(message = "更新时ID不能为空") // 为 ID 添加校验
        private Long id; // 新增 ID 字段

        @NotBlank(message = "企业名称不能为空")
        @Size(max = 20, message = "企业名称不能超过20个字符")
        private String name;

        @NotNull(message = "合作类型不能为空")
        private com.shm.demo.entity.CooperationType cooperationType;

        @NotNull(message = "企业类型不能为空")
        private com.shm.demo.entity.EnterpriseType enterpriseType;

        private String region;
    }
}