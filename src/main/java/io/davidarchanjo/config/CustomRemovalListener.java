package io.davidarchanjo.config;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRemovalListener implements RemovalListener<Object, Object>{

    @Override
    public void onRemoval(Object key, Object value, RemovalCause cause) {
        log.warn("removal listerner called with key {}, cause {}, evicted {}", key, cause.toString(), cause.wasEvicted());
    }
}