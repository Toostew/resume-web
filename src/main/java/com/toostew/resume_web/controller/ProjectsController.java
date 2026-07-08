package com.toostew.resume_web.controller;


import com.toostew.resume_web.DAO.FileDAO;
import com.toostew.resume_web.DAO.PostDAO;
import com.toostew.resume_web.DAO.ProjectsDAO;
import com.toostew.resume_web.DAO.ThumbnailDAO;
import com.toostew.resume_web.entity.Post;
import com.toostew.resume_web.entity.Projects;
import com.toostew.resume_web.entity.Thumbnail;
import com.toostew.resume_web.exception.ControllerException;
import com.toostew.resume_web.exception.DAOException;
import com.toostew.resume_web.exception.R2ServiceException;
import com.toostew.resume_web.service.R2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Controller
public class ProjectsController {

    @Value("${service.code}")
    private String serviceCode;

    @Value("${Bucket.Name}")
    private String bucketName;

    private R2Service r2Service;
    private PostDAO postDAO;
    private ThumbnailDAO thumbnailDAO;
    private FileDAO fileDAO;
    private ProjectsDAO projectsDAO;

    public ProjectsController(R2Service r2Service, PostDAO postDAO,
                              ThumbnailDAO thumbnailDAO, FileDAO fileDAO,
                              ProjectsDAO projectsDAO) {

        this.r2Service = r2Service;
        this.postDAO = postDAO;
        this.thumbnailDAO = thumbnailDAO;
        this.fileDAO = fileDAO;
        this.projectsDAO = projectsDAO;
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        //this will be projects where featured = 0, or if there are none, the latest project
        model.addAttribute("FeaturedProjects", projectsDAO.getFeaturedProjectsOrLatest());

        //this will be projects that are strictly non-featured
        model.addAttribute("PastProjects", projectsDAO.getNonFeaturedProjects());

        return "project-front";
    }

    @GetMapping("/projects/upload")
    public String projectUpload() {
        return "project-upload";
    }

    @PostMapping("/projects/upload/process")
    public String processProjectUpload(@RequestParam(name = "projectName") String projectName,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                       @RequestParam(name = "status") String status,
                                       @RequestParam(name = "content") String content,
                                       @RequestParam(name = "thumbnail")MultipartFile thumbnail,
                                       @RequestParam(name = "code") String code) {


        //check given code
        if(!code.equalsIgnoreCase(serviceCode)){
            return "redirect:/projects"; //send em back to arkham asylum
        }

        //create new thumbnail and upload
        Thumbnail thumbnailObj = new Thumbnail();
        UUID uuid = UUID.randomUUID();
        LocalDate date = LocalDate.now(); //reuse this


        thumbnailObj.setStored_name(uuid.toString() + "-thumbnail");
        thumbnailObj.setOriginal_name(thumbnail.getOriginalFilename());
        thumbnailObj.setSize(thumbnail.getSize());
        thumbnailObj.setContent_type(thumbnail.getContentType());
        thumbnailObj.setDate_created(date);
        Thumbnail recaptureThumbnail = new Thumbnail();
        try{
            recaptureThumbnail = thumbnailDAO.create(thumbnailObj);
        } catch(DAOException e){
            throw new ControllerException("Issue in ProjectsController: could not create thumbnail", e);
        } catch(Exception e){
            throw new ControllerException("Issue in ProjectsController: could not create thumbnail", e);
        }

        System.out.println("creating new project");
        Projects projectObj = new Projects();
        projectObj.setThumbnail(thumbnailObj);
        projectObj.setProjectName(projectName);
        projectObj.setDescription(description);
        projectObj.setStartDate(startDate);
        projectObj.setEndDate(endDate);
        projectObj.setStatus(status);
        projectObj.setContent(content);
        //featured is not set during upload, you do it yourself

        System.out.println("created ProjectObj");
        try{
            System.out.println("uploading Project");
            projectsDAO.createProjects(projectObj);
        } catch (DAOException e){
            throw new ControllerException("Issue in ProjectsController: could not create Project, DAO issue", e);
        } catch(Exception e){
            throw new ControllerException("Issue in ProjectsController: could not create Project, unknown issue", e);
        }
        System.out.println("uploaded Project");

        //if no issues, upload thumbnail to R2
        try{
            byte[] thumbnailByte = thumbnail.getBytes();
            r2Service.postObjectWithBucketAndKey(bucketName, recaptureThumbnail.getStored_name(), thumbnailByte, recaptureThumbnail.getSize(), recaptureThumbnail.getContent_type());
        } catch (IOException e) {
            throw new ControllerException("Issue in ProjectController: could not upload thumbnail to R2, IO issue", e);
        } catch(R2ServiceException e){
            throw new ControllerException("Issue in ProjectController: could not upload thumbnail to R2, R2Service issue", e);
        } catch(Exception e){
            throw new ControllerException("Issue in ProjectController: could not upload thumbnail to R2, unknown issue", e);
        }

        return "redirect:/projects";
    }

    //print the content as literal HTML
    @GetMapping("/projects/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> renderBlog(@PathVariable int id) {
        try {
            Projects temp = projectsDAO.readProjects(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(temp.getContent().getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new ControllerException("Issue in BlogController: could not render raw data", e);
        }
    }

}
