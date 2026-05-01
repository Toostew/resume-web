package com.toostew.resume_web.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class AWSServiceClientSource {


    //these values should absolutely NOT be hardcoded in for security reasons dummy
    @Value("${Access.Key.ID}")
    private String accessKeyId;

    @Value("${Token.Value}")
    private String tokenValue;

    @Value("${Secret.Access.Key}")
    private String secretAccessKey;

    @Value("${r2.API.URL}")
    private String r2URL;


    //every aws service we want to access requires a singleton instance
    @Bean
    public S3Client getS3Client(){


        //NOTE: R2 Does not utilize session tokens because sessions are validated by AWS for
        //Identity and Access Management (IAM) and Security Token Service, both not used by R2
        //NOTE2: we specify the URI to point to cloudflare, following cloudflare convention

            S3Client s3Client = S3Client.builder()
                    .region(Region.of("auto")) // set region to auto, per R2 specs
                    .endpointOverride( //set the endpoint to R2 from cloudflare
                            URI.create(r2URL))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId,secretAccessKey)))
                    .serviceConfiguration(S3Configuration.builder()
                            .checksumValidationEnabled(false) //at the current moment, there is no better alternative
                            .build())
                    .build();

            return s3Client;

        //NOTE: this is a S3Client Builder, no calls are made here thus no exceptions can actually occur here



    }

}
