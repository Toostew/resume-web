package com.toostew.resume_web.controller;


import com.toostew.resume_web.DAO.PostDAO;
import com.toostew.resume_web.DAO.ThumbnailDAO;
import com.toostew.resume_web.entity.Post;
import com.toostew.resume_web.entity.Thumbnail;
import com.toostew.resume_web.exception.ControllerException;
import com.toostew.resume_web.exception.DAOException;
import com.toostew.resume_web.exception.R2ServiceException;
import com.toostew.resume_web.service.R2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Controller
public class BlogController {

    @Value("${service.code}")
    private String serviceCode;

    @Value("${Bucket.Name}")
    private String bucketName;

    private R2Service r2Service;
    private PostDAO postDAO;
    private ThumbnailDAO thumbnailDAO;

    public BlogController(R2Service r2Service, PostDAO postDAO, ThumbnailDAO thumbnailDAO) {
        this.r2Service = r2Service;
        this.postDAO = postDAO;
        this.thumbnailDAO = thumbnailDAO;
    }

    @GetMapping("/blog")
    public String getBlogPage(Model model) {
        return "blog-front";
    }

    @GetMapping("/blog/upload")
    public String uploadBlogPage(){
        return "blog-upload";
    }

    //processing
    @PostMapping("/blog/upload/process")
    public String uploadBlogPage(@RequestParam(name = "blogMD") String post,
                                 @RequestParam(name = "title") String postTitle,
                                 @RequestParam(name = "description") String description,
                                 @RequestParam(name = "thumbnail") MultipartFile thumbnailFile,
                                 @RequestParam(name = "code") String code){

        if(!code.equalsIgnoreCase(serviceCode)){
            return "redirect:/blog/upload"; //if code not the same then cancel
        }
        //create new thumbnail first and upload
        Thumbnail thumbnailObj = new Thumbnail();
        UUID uuid = UUID.randomUUID();
        LocalDate date = LocalDate.now();


        thumbnailObj.setStored_name(uuid.toString());
        thumbnailObj.setOriginal_name(thumbnailFile.getOriginalFilename());
        thumbnailObj.setSize(thumbnailFile.getSize());
        thumbnailObj.setContent_type(thumbnailFile.getContentType());
        thumbnailObj.setDate_created(date);
        Thumbnail recaptureThumbnail = new  Thumbnail();
        try{
            recaptureThumbnail = thumbnailDAO.create(thumbnailObj);
        } catch(DAOException e){
            throw new ControllerException("Issue in BlogController: could not create thumbnail", e);
        } catch(Exception e){
            throw new ControllerException("Issue in BlogController: could not create thumbnail", e);
        }


        System.out.println("creating PostObj");
        Post postObj = new Post();
        postObj.setTitle(postTitle);
        postObj.setDescription(description);
        postObj.setContent(post);
        postObj.setThumbnail(recaptureThumbnail);
        postObj.setUploadDate(date);
        postObj.setTitleURLFriendly(uuid.toString());
        System.out.println("created PostObj");
        try{
            System.out.println("uploading Post");
            postDAO.create(postObj);
        } catch (DAOException e){
            throw new ControllerException("Issue in BlogController: could not create post, DAO issue", e);
        } catch(Exception e){
            throw new ControllerException("Issue in BlogController: could not create post, unknown issue", e);
        }
        System.out.println("uploaded Post");

        //if no issues, upload thumbnail to R2
        try{
            byte[] thumbnailByte = thumbnailFile.getBytes();
            r2Service.postObjectWithBucketAndKey(bucketName, recaptureThumbnail.getStored_name(), thumbnailByte, recaptureThumbnail.getSize(), recaptureThumbnail.getContent_type());
        } catch (IOException e) {
            throw new ControllerException("Issue in BlogController: could not upload thumbnail to R2, IO issue", e);
        } catch(R2ServiceException e){
            throw new ControllerException("Issue in BlogController: could not upload thumbnail to R2, R2Service issue", e);
        } catch(Exception e){
            throw new ControllerException("Issue in BlogController: could not upload thumbnail to R2, unknown issue", e);
        }




        return "redirect:/blog";
    }

}
