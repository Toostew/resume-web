package com.toostew.resume_web.controller;


import com.toostew.resume_web.DAO.FileDAO;
import com.toostew.resume_web.DAO.PostDAO;
import com.toostew.resume_web.DAO.ThumbnailDAO;
import com.toostew.resume_web.entity.Post;
import com.toostew.resume_web.entity.R2File;
import com.toostew.resume_web.entity.Thumbnail;
import com.toostew.resume_web.exception.ControllerException;
import com.toostew.resume_web.exception.DAOException;
import com.toostew.resume_web.exception.R2ServiceException;
import com.toostew.resume_web.service.R2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
    private FileDAO fileDAO;

    public BlogController(R2Service r2Service, PostDAO postDAO, ThumbnailDAO thumbnailDAO, FileDAO fileDAO) {
        this.r2Service = r2Service;
        this.postDAO = postDAO;
        this.thumbnailDAO = thumbnailDAO;
        this.fileDAO = fileDAO;
    }

    @GetMapping("/blog")
    public String getBlogPage(Model model) {
        model.addAttribute("numberOfPosts", postDAO.getTotalNumberOfPosts());
        model.addAttribute("ListOfAllPosts", postDAO.getAllPosts());
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
                                 @RequestParam(name = "PostType") String postType,
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
        Thumbnail recaptureThumbnail = new Thumbnail();
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
        postObj.setPtype(postType);

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


    //render image, not thumbnail
    @GetMapping("/blog/render-image/{id}")
    @ResponseBody
    public ResponseEntity<Resource> renderImage(@PathVariable int id){
        try{
            R2File temp = fileDAO.readFile(id);
            ResponseEntity<Resource> R2Object = r2Service.getObject(bucketName, temp.getStored_name());
            return R2Object;
        } catch (R2ServiceException e){
            throw new ControllerException("Issue in BlogController: could not read R2 file from R2Service; R2 Service Issue", e);
        } catch (DAOException e){
            throw new ControllerException("Issue in BlogController: could not read R2 file from R2Service; DAO issue", e);
        } catch (Exception e){
            throw new ControllerException("Issue in BlogController: could not read R2 file from R2Service; unknown issue", e);
        }
    }

    //render thumbnail of post
    @GetMapping("/blog/render-thumbnail/{id}")
    @ResponseBody
    public ResponseEntity<Resource> renderThumbnail(@PathVariable int id){
        try{
            Thumbnail temp = thumbnailDAO.readFile(id);
            ResponseEntity<Resource> R2Object = r2Service.getObject(bucketName, temp.getStored_name());
            return R2Object;
        } catch (R2ServiceException e){
            throw new ControllerException("Issue in BlogController: could not read R2 file from R2Service; R2 Service Issue", e);
        } catch (DAOException e){
            throw new ControllerException("Issue in BlogController: could not read R2 file from R2Service; DAO issue", e);
        } catch (Exception e){
            throw new ControllerException("Issue in BlogController: could not read R2 file from R2Service; unknown issue", e);
        }
    }

    //renders the blog from the contents of the posthtml
    @GetMapping("/blog/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> renderBlog(@PathVariable int id) {
        try {
            Post temp = postDAO.read(id);

            if ("post".equalsIgnoreCase(temp.getPtype())) {
                // If it's a post, we still return HTML, but we have to change the return type to
                // handle byte arrays, so we convert the String to bytes.
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(temp.getContent().getBytes(StandardCharsets.UTF_8));
            }

            else if ("image".equalsIgnoreCase(temp.getPtype())) {
                // Case: No fluff, just the pixels.
                Thumbnail thumb = temp.getThumbnail();

                // Fetch the actual binary data from R2
                ResponseEntity<Resource> r2ServiceObject = r2Service.getObject(bucketName,temp.getThumbnail().getStored_name());
                byte[] imageBytes = r2ServiceObject.getBody().getContentAsByteArray();
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(thumb.getContent_type()))
                        .contentLength(thumb.getSize())
                        .body(imageBytes);
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            throw new ControllerException("Issue in BlogController: could not render raw data", e);
        }
    }


}
