package com.company.ratelimiter.unit;

import com.company.ratelimiter.scripts.LuaScriptLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Lua script loading
 */
class LuaScriptLoaderTest {

    private LuaScriptLoader scriptLoader;

    @BeforeEach
    void setUp() {
        scriptLoader = new LuaScriptLoader();
    }

    @Test
    void testLoadSlidingWindowLogScript() {
        String script = scriptLoader.getSlidingWindowLogScript();
        
        assertThat(script).isNotNull();
        assertThat(script).isNotEmpty();
        assertThat(script).contains("KEYS[1]");
        assertThat(script).contains("ARGV[1]");
        assertThat(script).contains("ZADD");
        assertThat(script).contains("ZREMRANGEBYSCORE");
        assertThat(script).contains("ZCARD");
    }

    @Test
    void testLoadSlidingWindowMultiScript() {
        String script = scriptLoader.getSlidingWindowMultiScript();
        
        assertThat(script).isNotNull();
        assertThat(script).isNotEmpty();
        assertThat(script).contains("num_dimensions");
        assertThat(script).contains("for i = 1, num_dimensions do");
    }

    @Test
    void testScriptCaching() {
        String script1 = scriptLoader.getSlidingWindowLogScript();
        String script2 = scriptLoader.getSlidingWindowLogScript();
        
        // Should return the same instance (cached)
        assertThat(script1).isSameAs(script2);
    }

    @Test
    void testLoadNonExistentScript() {
        assertThatThrownBy(() -> scriptLoader.loadScript("non_existent"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to load Lua script");
    }

    @Test
    void testClearCache() {
        scriptLoader.getSlidingWindowLogScript();
        scriptLoader.clearCache();
        
        String script = scriptLoader.getSlidingWindowLogScript();
        assertThat(script).isNotNull();
    }
}
