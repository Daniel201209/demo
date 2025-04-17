package com.shm.demo.dto;

import javax.validation.Valid;
import javax.validation.constraints.*;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreatePersonnelRequest {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 10, message = "姓名长度不能超过10个字符")
    private String name;

    @NotNull(message = "性别不能为空")
    @Min(value = 1, message = "性别值无效")
    @Max(value = 2, message = "性别值无效") // 假设 1=男, 2=女
    private Byte gender;

    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 999, message = "年龄不能超过3位数")
    private Integer age;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须是11位数字")
    private String phone;

    @NotNull(message = "学历不能为空")
    private Byte education; // 可以定义枚举或常量来表示学历

    @NotNull(message = "参加工作时间不能为空")
    @PastOrPresent(message = "参加工作时间不能是未来日期")
    private LocalDate startWorkDate;

    @NotNull(message = "任职企业ID不能为空")
    private Long enterpriseId;
}