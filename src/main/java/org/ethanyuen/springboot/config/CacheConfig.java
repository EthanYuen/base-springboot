package org.ethanyuen.springboot.config;

import net.sf.ehcache.CacheManager;
import org.nutz.plugins.cache.dao.DaoCacheInterceptor;
import org.nutz.plugins.cache.dao.impl.convert.JavaCacheSerializer;
import org.nutz.plugins.cache.dao.impl.provider.EhcacheDaoCacheProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig  {
    @Bean
    public CacheManager cacheManager() {
        return CacheManager.create();
    }
    @Bean
    public EhcacheDaoCacheProvider cacheProvider(CacheManager cacheManager){
        EhcacheDaoCacheProvider ehcacheDaoCacheProvider = new EhcacheDaoCacheProvider();
        ehcacheDaoCacheProvider.setCacheManager(cacheManager);
        ehcacheDaoCacheProvider.setSerializer(new JavaCacheSerializer());
        return ehcacheDaoCacheProvider;
    }
    @Bean
    public DaoCacheInterceptor daoCacheInterceptor(EhcacheDaoCacheProvider cacheProvider){
        DaoCacheInterceptor daoCacheInterceptor = new DaoCacheInterceptor();
        daoCacheInterceptor.setCacheProvider(cacheProvider);
        daoCacheInterceptor.setCachedTableNamePatten("^((?!system_log).)*$");
        daoCacheInterceptor.setCache4Null(true);
        daoCacheInterceptor.setEnableWhenTrans(true);
       return daoCacheInterceptor;
    }
}
