package com.toostew.resume_web.controller;


import com.toostew.resume_web.dto.RepoReturnObject;
import com.toostew.resume_web.service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@org.springframework.stereotype.Controller
public class PortfolioController {

    private GithubService gitHubService;


    public PortfolioController(GithubService gitHubService) {
        this.gitHubService = gitHubService;
    }


    @GetMapping("/")
    public String getPortfolio(Model model) {
        // Fetch the projects from our cached service
        List<RepoReturnObject> repos = gitHubService.getCombinedData();


        model.addAttribute("projects", repos);


        return "portfolio";
    }

    @GetMapping("/thumbnail/profilepic")
    public ResponseEntity<Resource> getProfilePic(){
        Resource resource = new ClassPathResource("static/pfp.jpg");
        return ResponseEntity.ok()
                .body(resource);
    }
}
