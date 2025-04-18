package com.shm.demo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode; // 用于继承 PaginationRequest 的 equals/hashCode
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true) // 继承父类的 equals 和 hashCode
public class SearchCooperationRequest extends PaginationRequest { // 继承分页请求

    @Size(max = 20, message = "搜索的合作主题不能超过20个字符")
    private String cooperationTheme; // 合作主题 (模糊搜索)

    @Pattern(regexp = "^(北京|广州|上海)$", message = "发起方必须是北京、广州或上海")
    private String initiatorRegion; // 发起方 (精确匹配)

    @Pattern(regexp = "^(北京|广州|上海)$", message = "接收方必须是北京、广州或上海")
    private String receiverRegion; // 接收方 (精确匹配)

    // 重置功能由前端实现，后端只需处理传入的搜索条件
}