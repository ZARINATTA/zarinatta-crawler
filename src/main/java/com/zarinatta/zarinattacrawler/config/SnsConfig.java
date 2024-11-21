package com.zarinatta.zarinattacrawler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class SnsConfig {

    @Value("${aws.sns.access-key}")
    private String awsAccessKey;

    @Value("${aws.sns.secret-key}")
    private String awsSecretKey;

    @Value("${aws.sns.region}")
    private String awsRegion;

    @Value("${aws.sns.arn}")
    private String snsArn;

    @Bean
    public AwsCredentialsProvider awsBasicCredentials() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        return StaticCredentialsProvider.create(awsBasicCredentials);
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .credentialsProvider(awsBasicCredentials())
                .region(Region.of(awsRegion))
                .build();
    }
}
