package com.shm.demo.controller;

import com.github.pagehelper.PageInfo; // 引入 PageInfo
import com.shm.demo.dto.SearchEnterpriseRequest; // 引入请求 DTO
import com.shm.demo.entity.Enterprise;
import com.shm.demo.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid; // 注意：Spring Boot 3 使用 jakarta.validation
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

    // ... EnterpriseRequest DTO, createEnterprise, getAllEnterprises, getEnterpriseById, updateEnterprise, deleteEnterprise 方法不变 ...
    // ... POST /api/enterprises/add (createEnterprise) ...
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


    // ... GET /api/enterprises/getAllEnterprises (getAllEnterprises) ...
    @GetMapping("/getAllEnterprises")
    public ResponseEntity<List<Enterprise>> getAllEnterprises() {
        // ... (代码不变) ...
        List<Enterprise> enterprises = enterpriseService.getAllEnterprises();
        return ResponseEntity.ok(enterprises);
    }

    // ... POST /api/enterprises/getId (getEnterpriseById) ...
     @PostMapping("/getId")
    public ResponseEntity<?> getEnterpriseById(@RequestBody Long id) {
        // ... (代码不变) ...
        if (id == null) {
             return ResponseEntity.badRequest().body("ID 不能为空");
        }
        Enterprise enterprise = enterpriseService.getEnterpriseById(id);
        if (enterprise != null) {
            return ResponseEntity.ok(enterprise);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到ID为 " + id + " 的企业或已被删除");
        }
    }


    // ... POST /api/enterprises/updataDataId (updateEnterprise) ...
    @PostMapping("/updataDataId") // 建议遵循 RESTful 风格用 PUT /api/enterprises/{id}
    public ResponseEntity<?> updateEnterprise(@Valid @RequestBody EnterpriseRequest request) {
         // ... (代码不变) ...
         try {
            // 从 request 对象中获取 ID
            Long id = request.getId();
            // 可以在这里加一个额外的 null 判断，虽然 @NotNull 应该已经处理了
            if (id == null) {
                 return ResponseEntity.badRequest().body("请求体中必须包含有效的ID");
            }

            // 使用从 request 获取的 id 查询 (Service 层会处理找不到的情况)
            // Enterprise existing = enterpriseService.getEnterpriseById(id);
            // if (existing == null) {
            //      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到ID为 " + id + " 的企业或已被删除");
            // }

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
            // Service 层抛出的异常 (包括校验失败、找不到等)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新企业时发生内部错误");
        }
    }


    // ... POST /api/enterprises/deleteId (deleteEnterprise) ...
    @PostMapping("/deleteId") // 建议遵循 RESTful 风格用 DELETE /api/enterprises/{id}
    public ResponseEntity<?> deleteEnterprise(@RequestBody Long id) {
        // ... (代码不变) ...
        if (id == null) {
             return ResponseEntity.badRequest().body("ID 不能为空");
        }
        try {
            enterpriseService.deleteEnterprise(id); // 调用的是逻辑删除的 Service 方法
            // return ResponseEntity.noContent().build(); // 标准 RESTful 返回 204 No Content
            return ResponseEntity.status(HttpStatus.OK).body("删除成功"); // 根据你的 API 规范返回
        } catch (IllegalArgumentException e) {
             // 如果 service 层在找不到或已被删除时抛出异常
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除企业时发生内部错误");
        }
    }

    // --- 新增搜索接口 ---
    /**
     * 根据条件搜索企业信息 (分页)
     * @param request 包含搜索条件和分页参数的请求对象
     * @return 分页后的企业信息列表 (PageInfo<Enterprise>)
     */
    @PostMapping("/search") // 使用 POST 接收包含多个条件的请求体
    public ResponseEntity<?> searchEnterprises(@RequestBody SearchEnterpriseRequest request) {
        try {
            // 调用 Service 层进行搜索和分页
            PageInfo<Enterprise> enterprisePageInfo = enterpriseService.searchEnterprises(request);
            // 直接返回 PageInfo<Enterprise>，前端可以解析里面的分页数据和列表
            return ResponseEntity.ok(enterprisePageInfo);
        } catch (IllegalArgumentException e) {
            // Service 层可能抛出校验异常 (例如无效地区)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // log.error("Error searching enterprises", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("搜索企业时发生内部错误");
        }
    }

    // --- DTO for Request Body ---
    @lombok.Data
    static class EnterpriseRequest {
        // @NotNull(message = "更新时ID不能为空") // 更新时 ID 从路径获取更符合 RESTful
        private Long id; // ID 字段保留，用于从 RequestBody 接收

        @NotBlank(message = "企业名称不能为空")
        @Size(max = 20, message = "企业名称不能超过20个字符")
        private String name;

        @NotNull(message = "合作类型不能为空")
        private com.shm.demo.entity.CooperationType cooperationType;

        @NotNull(message = "企业类型不能为空")
        private com.shm.demo.entity.EnterpriseType enterpriseType;

        // 地区可以为空，但如果提供，Service 层会校验
        private String region;
    }
}