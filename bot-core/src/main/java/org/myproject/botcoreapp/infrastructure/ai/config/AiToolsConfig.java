package org.myproject.botcoreapp.infrastructure.ai.config;

import org.myproject.botcoreapp.infrastructure.ai.tools.DateTimeTool;
import org.myproject.botcoreapp.infrastructure.ai.tools.NoteTool;
import org.myproject.botcoreapp.infrastructure.ai.tools.TelBookTool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiToolsConfig {

    @Bean
    public List<Object> aiTools(NoteTool noteTool, DateTimeTool dateTimeTool, TelBookTool telBookTool) {
        return List.of(noteTool, dateTimeTool, telBookTool);
    }
}
