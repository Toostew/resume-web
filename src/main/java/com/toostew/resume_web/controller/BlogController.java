package com.toostew.resume_web.controller;


import com.toostew.resume_web.service.R2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Controller
public class BlogController {

    @Value("${service.code}")
    private String serviceCode;

    private R2Service r2Service;

    public BlogController(R2Service r2Service) {
        this.r2Service = r2Service;
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
    public String uploadBlogPage(@RequestParam(name = "blogFile") MultipartFile postFile,
                                 @RequestParam(name = "title") String postTitle,
                                 @RequestParam(name = "description") String description,
                                 @RequestParam(name = "thumbnail") MultipartFile thumbnail,
                                 @RequestParam(name = "code") String code){

        if(code.toLowerCase() != serviceCode.toLowerCase()){
            return "redirect:/blog/upload"; //if code no the same then cancel
        }



        return "redirect:/blog";
    }

}
