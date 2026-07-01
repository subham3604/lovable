package com.subham.projects.lovableClone.config;

import io.minio.MinioClient;
import lombok.Data;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class StorageConfig {
    private String url;
    private String accessKey;
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(
                        ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.COMPATIBLE_TLS,
                        ConnectionSpec.CLEARTEXT
                ))
                .build();

        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .httpClient(httpClient)
                .build();
    }
}
