package com.toostew.resume_web.service;


import com.toostew.resume_web.config.AWSServiceClientSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import com.toostew.resume_web.exception.R2ServiceException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class R2Service {

    private S3Client s3Client;

    @Value("${Bucket.Name}")
    private String bucketName;



    public R2Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public ResponseEntity<Resource> getObject(String bucket, String key){
        try{
            //get object via stream
            //ResponseInputStream is an implementation of InputStream,
            //incoming data does not arrive all at once, we need to collect it, like running water into a bail
            //we "pipe it along" once our bail is full (we have the full file in bytes)
            //we never convert it or anything, we just hold it's raw bytecode, what it is and what to do with it is
            //dictated by headers of our output, if we say to treat it like a png, it'll be treated like one (even if its not)
            //InputStreams happens once per call and we can only use it once,
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
            //return object metadata, we need the filetype
            GetObjectResponse response = responseInputStream.response();
            String contentType = response.contentType(); //we now have the retrieved item's datatype
            long size = response.contentLength();

            //InputStreamResource is a Resource implementation of InputStream
            //we can fill the body of a http response with a resource, so we must make a resource
            //we have an inputStream (ResourceInputStream), we can use InputStreamResource to convert an inputStream into a valid resource
            //we can also pass a description for later
            InputStreamResource resource = new InputStreamResource(responseInputStream,"description");


            //here we build a http response
            return ResponseEntity.ok()
                    .contentType(contentTypeDetect(contentType))
                    .body(resource);
            //we do not need to specify .build() after placing a .body()

        } catch (AwsServiceException e) {
            //R2AWS Client issue
            throw new R2ServiceException("Issue with Get Object at R2Service layer, R2 Server issue",e);
        } catch (SdkClientException e) {
            //SDK client side issue
            throw new R2ServiceException("Issue with Get Object at R2Service layer, client issue",e);
        }
    }

    public void postObjectWithBucketAndKey(String bucket, String key, byte[] item, long size, String contentType){
        try(InputStream inputStream = new ByteArrayInputStream(item)){

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromInputStream(inputStream,size)
                    //the size MUST be precise, else we risk failed uploads
            );

        } catch (AwsServiceException e){
            throw new R2ServiceException("Issue with Post Object at R2Service layer, R2 Server issue",e);
        } catch (SdkClientException e){
            throw new R2ServiceException("Issue with Post Object at R2Service layer, client issue",e);
        } catch (IOException e){
            throw new R2ServiceException("Issue with Post Object at R2Service layer, client issue",e);
        }

    }



    //when getting an object from R2, we are crafting a HTTP response, and in it, we need to specify a contentType
    //contentType only accepts it in the format of Mediatype, so we parse the String ContentType through here to get a mediaType match
    private MediaType contentTypeDetect(String contentType){
        //images
        if(contentType == null){
            return null;
        }
        else if(contentType.equals("image/jpeg") || contentType.equals("image/jpg")){
            return MediaType.IMAGE_JPEG;
        }
        else if(contentType.equals("image/png")){
            return MediaType.IMAGE_PNG;
        }
        else if(contentType.equals("image/gif")){
            return MediaType.IMAGE_GIF;
        }


        //pdf
        else if(contentType.equals("application/pdf")){
            return MediaType.APPLICATION_PDF;
        }

        return null;

    }



}
