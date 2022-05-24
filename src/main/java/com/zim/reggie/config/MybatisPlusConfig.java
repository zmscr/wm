package com.zim.reggie.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置MP分页插件
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor (){
        MybatisPlusInterceptor mp = new MybatisPlusInterceptor();
        mp.addInnerInterceptor(new PaginationInnerInterceptor());
        return mp;
    }

}
