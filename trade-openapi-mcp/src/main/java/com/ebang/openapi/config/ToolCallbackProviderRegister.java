package com.ebang.openapi.config;

import com.ebang.openapi.service.*;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolCallbackProviderRegister {

    @Bean
    public ToolCallbackProvider ibkrTools(IBKRMcpService ibkrMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(ibkrMcpService).build();
    }
    @Bean
    public ToolCallbackProvider futuTools(FutuMcpService futuMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(futuMcpService).build();
    }
    @Bean
    public ToolCallbackProvider longportTools(LongportMcpService longportMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(longportMcpService).build();
    }
    @Bean
    public ToolCallbackProvider hstongTools(HSTongMcpService hsTongMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(hsTongMcpService).build();
    }
    @Bean
    public ToolCallbackProvider webullTools(WebullMcpService webullMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(webullMcpService).build();
    }
    @Bean
    public ToolCallbackProvider tigerTools(TigerMcpService tigerMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(tigerMcpService).build();
    }
    @Bean
    public ToolCallbackProvider robinhoodTools(RobinhoodMcpService robinhoodMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(robinhoodMcpService).build();
    }
    @Bean
    public ToolCallbackProvider usmartTools(USmartMcpService uSmartMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(uSmartMcpService).build();
    }
}
