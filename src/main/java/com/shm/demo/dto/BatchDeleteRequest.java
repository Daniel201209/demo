package com.shm.demo.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class BatchDeleteRequest {

    @NotEmpty(message = "删除的ID列表不能为空")
    private List<@NotNull(message = "ID不能为空") Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}