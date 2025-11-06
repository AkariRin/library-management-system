package dev.rbq.library_management_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Redis Session 配置
 */
@Configuration
@EnableRedisHttpSession // 默认 30 分钟 (1800 秒)
public class RedisSessionConfig {
    @Value("${spring.session.cookie.secure:true}")
    private boolean useSecureCookie;


    /**
     * 配置 Session Cookie
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSIONID");
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setUseSecureCookie(useSecureCookie); // 从配置文件读取
        serializer.setSameSite("Lax");
        return serializer;
    }
}

