package com.shm.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 自定义异常类，用于表示请求的资源未找到。
 * 使用 @ResponseStatus 注解，当此异常未被捕获并抛出到 Controller 层之外时，
 * Spring MVC 会自动将其映射为 HTTP 404 Not Found 状态码。
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND) // 映射到 HTTP 404
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L; // 序列化版本号

    private String resourceName; // 未找到的资源名称 (例如 "Cooperation", "Personnel")
    private String fieldName;    // 用于查找资源的字段名称 (例如 "id", "theme")
    private Object fieldValue;   // 用于查找资源的字段值

    /**
     * 构造函数，包含详细的资源未找到信息。
     *
     * @param resourceName 资源名称
     * @param fieldName    字段名称
     * @param fieldValue   字段值
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        // 构建默认的错误消息
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * 构造函数，允许自定义错误消息。
     *
     * @param resourceName 资源名称
     * @param fieldName    字段名称
     * @param fieldValue   字段值
     * @param message      自定义错误消息
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue, String message) {
        super(message); // 使用传入的自定义消息
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    // --- Getters ---

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}