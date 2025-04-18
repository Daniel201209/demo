package com.shm.demo.dto;

import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCooperationRequest {

    @NotBlank(message = "合作主题不能为空")
    @Size(max = 20, message = "合作主题不能超过20个字符")
    private String cooperationTheme;

    @NotBlank(message = "发起方不能为空")
    @Pattern(regexp = "^(北京|广州|上海)$", message = "发起方必须是北京、广州或上海")
    private String initiatorRegion;

    @NotBlank(message = "接收方不能为空")
    @Pattern(regexp = "^(北京|广州|上海)$", message = "接收方必须是北京、广州或上海")
    private String receiverRegion;

    @NotNull(message = "合作开始时间不能为空")
    // @FutureOrPresent(message = "合作开始时间不能早于当前时间") // 业务逻辑在 Service 层校验更灵活
    private LocalDate cooperationStartDate;

    @NotNull(message = "合作结束时间不能为空")
    private LocalDate cooperationEndDate;

    @NotEmpty(message = "合作人员名单不能为空")
    @Size(min = 1, message = "合作人员名单至少需要一项")
    @Valid // 嵌套校验 List 中的 CooperationPersonnelRequest 对象
    private List<CooperationPersonnelRequest> cooperationPersonnelList;

    // 可以在这里添加自定义校验注解来校验 cooperationEndDate >= cooperationStartDate
    // 但通常在 Service 层处理更方便
}