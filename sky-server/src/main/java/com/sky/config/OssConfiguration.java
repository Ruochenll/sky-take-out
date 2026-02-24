package com.sky.config;

import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云文件上传配置类
 */
@Configuration  // 表示当前类是一个配置类
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean // 当容器中没有这个bean时，才创建这个bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) throws ClientException {
        log.info("开始创建阿里云文件上传工具类：{}", aliOssProperties);
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider(),
                aliOssProperties.getBucketName(),
                aliOssProperties.getRegion()
        );
    }

}
