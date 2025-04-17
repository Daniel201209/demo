package com.shm.demo.dto;

import javax.validation.Valid;
import javax.validation.constraints.*;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdatePersonnelRequest {

    @NotNull(message = "更新时ID不能为空")
    private Long id;

    @Size(max = 10, message = "姓名长度不能超过10个字符")
    private String name; // 更新时字段变为可选

    @Min(value = 1, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    private Byte gender;

    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 999, message = "年龄不能超过3位数")
    private Integer age;

    @Pattern(regexp = "^\\d{11}$", message = "手机号必须是11位数字")
    private String phone;

    private Byte education;

    @PastOrPresent(message = "参加工作时间不能是未来日期")
    private LocalDate startWorkDate;

    private Long enterpriseId;
}