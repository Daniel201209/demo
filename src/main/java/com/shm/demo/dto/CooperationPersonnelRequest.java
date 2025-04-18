package com.shm.demo.dto;

import com.shm.demo.entity.CooperationJobType;
import lombok.Data;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CooperationPersonnelRequest {

    @NotNull(message = "送出企业不能为空")
    private Long sendingEnterpriseId;

    @NotNull(message = "合作人不能为空")
    private Long personnelId;

    @NotNull(message = "合作类型（工种）不能为空")
    private CooperationJobType cooperationJobType;

    @NotNull(message = "接收企业不能为空")
    private Long receivingEnterpriseId;

    @NotNull(message = "人员合作开始时间不能为空")
    // @FutureOrPresent(message = "人员合作开始时间不能早于当前时间") // 业务逻辑在 Service 层校验更灵活
    private LocalDate personnelStartDate;

    @NotNull(message = "人员拟合作结束时间不能为空")
    private LocalDate personnelEndDate;

    // 可以在这里添加校验逻辑，例如结束时间不能早于开始时间，但这通常在 Service 层处理更方便
}