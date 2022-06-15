package io.davidarchanjo.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;

@Configuration
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        String specAsString = "initialCapacity=100,maximumSize=500,expireAfterAccess=5m,recordStats";
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("CARS", "SECOND_CACHE");
        cacheManager.setAllowNullValues(false); //can happen if you get a value from a @Cachable that returns null
        //cacheManager.setCacheSpecification(specAsString);
        //cacheManager.setCaffeineSpec(caffeineSpec());
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    CaffeineSpec caffeineSpec() {
       return CaffeineSpec.parse("initialCapacity=100,maximumSize=500,expireAfterAccess=5m,recordStats");
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(150)
            .expireAfterAccess(Duration.ofMinutes(5))
            .weakKeys()
            .removalListener(new CustomRemovalListener())
            .recordStats();
    }

}
