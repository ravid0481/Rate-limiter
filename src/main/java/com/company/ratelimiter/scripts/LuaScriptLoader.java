package com.company.ratelimiter.scripts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and caches Lua scripts from resources.
 * Scripts are loaded once at startup and reused for all Redis calls.
 */
@Slf4j
@Component
public class LuaScriptLoader {

    private final Map<String, String> scriptCache = new ConcurrentHashMap<>();

    /**
     * Load a Lua script from classpath
     * 
     * @param scriptName Name of the script file (without .lua extension)
     * @return Lua script content
     */
    public String loadScript(String scriptName) {
        return scriptCache.computeIfAbsent(scriptName, this::loadScriptFromFile);
    }

    private String loadScriptFromFile(String scriptName) {
        try {
            String path = "lua/" + scriptName + ".lua";
            ClassPathResource resource = new ClassPathResource(path);
            
            if (!resource.exists()) {
                throw new IllegalArgumentException("Lua script not found: " + path);
            }

            String script = new String(
                resource.getInputStream().readAllBytes(), 
                StandardCharsets.UTF_8
            );

            log.info("Loaded Lua script: {} ({} bytes)", scriptName, script.length());
            return script;
            
        } catch (IOException e) {
            log.error("Failed to load Lua script: {}", scriptName, e);
            throw new RuntimeException("Failed to load Lua script: " + scriptName, e);
        }
    }

    /**
     * Get the sliding window log script
     */
    public String getSlidingWindowLogScript() {
        return loadScript("sliding_window_log");
    }

    /**
     * Get the multi-dimension sliding window script
     */
    public String getSlidingWindowMultiScript() {
        return loadScript("sliding_window_multi");
    }

    /**
     * Clear the script cache (useful for testing)
     */
    public void clearCache() {
        scriptCache.clear();
        log.info("Lua script cache cleared");
    }
}
