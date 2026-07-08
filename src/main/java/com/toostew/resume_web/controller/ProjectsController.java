package com.toostew.resume_web.controller;


import com.toostew.resume_web.DAO.FileDAO;
import com.toostew.resume_web.DAO.PostDAO;
import com.toostew.resume_web.DAO.ProjectsDAO;
import com.toostew.resume_web.DAO.ThumbnailDAO;
import com.toostew.resume_web.service.R2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

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

}
