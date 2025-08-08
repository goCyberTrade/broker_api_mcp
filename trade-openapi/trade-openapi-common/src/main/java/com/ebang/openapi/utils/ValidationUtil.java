package com.ebang.openapi.utils;

import com.ebang.openapi.exception.OpenApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.util.StringUtils;
import java.util.Set;

import static com.ebang.openapi.exception.OpenApiErrorCodeEnums.PARAM_ERROR;

/**
 * 通用校验工具类
 * 支持所有接口的渠道校验
 */
public class ValidationUtil {
    
    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    private static final jakarta.validation.Validator VALIDATOR = FACTORY.getValidator();
    // 定义校验分组
    public interface FutuGroup {}
    public interface HStongGroup {}
    public interface IBKRGroup {}
    public interface LongportGroup {}
    public interface RobinhoodGroup {}
    public interface TigerGroup {}
    public interface USmartGroup {}
    public interface WebullGroup {}

    /**
     * 通用渠道校验方法
     * @param request 请求对象
     * @param channel 渠道名称
     * @param enableAnnotationValidation 是否启用注解校验
     * @param enableBusinessValidation 是否启用业务逻辑校验
     */
    public static void validateByChannel(Object request, String channel,
                                boolean enableAnnotationValidation, 
                                boolean enableBusinessValidation) {
        if (!StringUtils.hasText(channel)) {
            throw new IllegalArgumentException("渠道名称不能为空");
        }
        // 1. 注解校验
        if (enableAnnotationValidation) {
            validateAnnotations(request, channel);
        }
    }
    
    /**
     * 简化版校验方法（默认启用所有校验）
     */
    public static void validateByChannel(Object request, String channel) {
        validateByChannel(request, channel, true, true);
    }
    
    /**
     * 根据渠道进行注解校验
     */
    private static void validateAnnotations(Object request, String channel) {
        Set<ConstraintViolation<Object>> violations;

        // 根据渠道选择对应的校验分组
        switch (channel.toLowerCase()) {
            case "futu":
                violations = VALIDATOR.validate(request, FutuGroup.class);
                break;
            case "hstong":
                violations = VALIDATOR.validate(request, HStongGroup.class);
                break;
            case "ibkr":
                violations = VALIDATOR.validate(request, IBKRGroup.class);
                break;
            case "longport":
                violations = VALIDATOR.validate(request, LongportGroup.class);
                break;
            case "rebinhood":
                violations = VALIDATOR.validate(request, RobinhoodGroup.class);
                break;
            case "tiger":
                violations = VALIDATOR.validate(request, TigerGroup.class);
                break;
            case "usmart":
                violations = VALIDATOR.validate(request, USmartGroup.class);
                break;
            case "webull":
                violations = VALIDATOR.validate(request, WebullGroup.class);
                break;
            default:
                // 默认不进行分组校验
                violations = VALIDATOR.validate(request);
                break;
        }
        
        if (!violations.isEmpty()) {
            throw new OpenApiException(PARAM_ERROR.getErrorCode(), violations.iterator().next().getPropertyPath()+" "+violations.iterator().next().getMessage());
        }
    }
    
    /**
     * 通用校验方法（支持指定分组）
     */
    public static <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("校验失败: " + violations.iterator().next().getMessage());
        }
    }
} 