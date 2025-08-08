package com.ebang.openapi.config;

import com.ebang.openapi.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.mcp.server.autoconfigure.McpServerProperties;
import org.springframework.ai.mcp.server.autoconfigure.McpServerStdioDisabledCondition;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.MimeType;
import org.springframework.web.servlet.function.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Conditional({McpServerStdioDisabledCondition.class})
@Slf4j
public class MyMcpWebServerAutoConfig {

    @Value("${spring.ai.mcp.server.sse-endpoint}")
    private String sseEndpoint;
    @Value("${spring.ai.mcp.server.sse-message-endpoint}")
    private String sseMessageEndpoint;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private Map<String, ToolCallbackProvider> toolCallbackProviderMap;

    @Bean
    public WebMvcSseServerTransportProviderLocal webMvcSseServerTransportProvider(ObjectProvider<ObjectMapper> objectMapperProvider, McpServerProperties serverProperties) {

        Map<String, String> toolData = getToolData(serverProperties);
        ObjectMapper objectMapper = (ObjectMapper) objectMapperProvider.getIfAvailable(ObjectMapper::new);
        return new WebMvcSseServerTransportProviderLocal(objectMapper, serverProperties.getBaseUrl(), serverProperties.getSseMessageEndpoint(), serverProperties.getSseEndpoint(), toolData);
    }

    @Bean
    public RouterFunction<ServerResponse> mvcMcpRouterFunction(WebMvcSseServerTransportProviderLocal transport) {
        return transport.getRouterFunction().filter(new HandlerFilterFunction<ServerResponse, ServerResponse>() {
            @Override
            public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {

                String path = request.path();
                if (sseEndpoint.equals(path)) {
                    // 获取参数
                    Optional<String> channel = request.param("channel");
                    Optional<String> apiKey = request.param("apiKey");
                    if (channel.isPresent() && apiKey.isPresent()) {
                        // 鉴权通过
                        if (authenticationService.checkApiKey(apiKey.get())) {
                            return next.handle(request);
                        }
                    }
                    return ServerResponse.status(HttpStatus.FORBIDDEN).build();
                } else if ("/mcp/message".equals(path)) {
                    // 获取请求路径（不包含查询参数）
                    String path1 = request.path();
                    // 示例输出: /api/users

                    // 获取路径变量（Path Variables）
                    Map<String, String> pathVariables = request.pathVariables();
                    // 若路径为 /api/users/{id}，则可通过 pathVariables.get("id") 获取值
                    String fullUri = request.uri().toString();
                    // 示例输出: [api, users]
                    // 获取表单数据（POST 请求）
                }
                // 放行
                ServerResponse handle = next.handle(request);
                return handle;
            }
        });
    }

    private Map<String, String> getToolData(McpServerProperties mcpServerProperties) {

        Map<String, String> rtnValue = new HashMap<>();

        for (Map.Entry<String, ToolCallbackProvider> entry : toolCallbackProviderMap.entrySet()) {

            String channel = entry.getKey().substring(0, entry.getKey().length() - 5);
            List<ToolCallback> tools = List.of(entry.getValue()).stream()
                    .map(pr -> List.of(pr.getToolCallbacks()))
                    .flatMap(List::stream)
                    .filter(fc -> fc instanceof ToolCallback)
                    .map(fc -> (ToolCallback) fc)
                    .toList();
            List<McpServerFeatures.SyncToolSpecification> syncTools = toSyncToolSpecifications(tools, mcpServerProperties);
            List<io.modelcontextprotocol.spec.McpSchema.Tool> showTools = syncTools.stream().map(McpServerFeatures.SyncToolSpecification::tool).toList();

            try {
                String toolDef = new ObjectMapper().writeValueAsString(showTools);
                rtnValue.put(channel, toolDef);
            } catch (Exception e) {
                log.error("转化工具列表异常", e);
            }
//            List<McpServerFeatures.AsyncToolSpecification> asyncTools = new ArrayList<>();
//            for (var tool : syncTools) {
//                asyncTools.add(fromSync(tool));
//            }
//
//            List<io.modelcontextprotocol.spec.McpSchema.Tool> showTools = asyncTools.stream().map(McpServerFeatures.AsyncToolSpecification::tool).toList();
//            String s2 = new ObjectMapper().writeValueAsString(showTools);

        }
        return rtnValue;
    }

    private List<McpServerFeatures.SyncToolSpecification> toSyncToolSpecifications(List<ToolCallback> tools,
                                                                                   McpServerProperties serverProperties) {

        // De-duplicate tools by their name, keeping the first occurrence of each tool
        // name
        return tools.stream() // Key: tool name
                .collect(Collectors.toMap(tool -> tool.getToolDefinition().name(), tool -> tool, // Value:
                        // the
                        // tool
                        // itself
                        (existing, replacement) -> existing)) // On duplicate key, keep the
                // existing tool
                .values()
                .stream()
                .map(tool -> {
                    String toolName = tool.getToolDefinition().name();
                    MimeType mimeType = (serverProperties.getToolResponseMimeType().containsKey(toolName))
                            ? MimeType.valueOf(serverProperties.getToolResponseMimeType().get(toolName)) : null;
                    return McpToolUtils.toSyncToolSpecification(tool, mimeType);
                })
                .toList();
    }

    McpServerFeatures.AsyncToolSpecification fromSync(McpServerFeatures.SyncToolSpecification tool) {
        // FIXME: This is temporary, proper validation should be implemented
        if (tool == null) {
            return null;
        }
        return new McpServerFeatures.AsyncToolSpecification(tool.tool(),
                (exchange, map) -> Mono
                        .fromCallable(() -> tool.call().apply(new McpSyncServerExchange(exchange), map))
                        .subscribeOn(Schedulers.boundedElastic()));
    }
}
