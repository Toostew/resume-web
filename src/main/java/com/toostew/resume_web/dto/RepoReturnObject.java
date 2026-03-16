package com.toostew.resume_web.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepoReturnObject {

    private String name;

    @JsonProperty("html_url")
    private String htmlURL;

    private String description;

    @JsonProperty("pushed_at")
    private String pushedAt;


    private Map<String, Integer> languages;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtmlURL() {
        return htmlURL;
    }

    public void setHtmlURL(String htmlURL) {
        this.htmlURL = htmlURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(String pushedAt) {
        this.pushedAt = pushedAt;
    }

    public Map<String, Integer> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<String, Integer> languages) {
        this.languages = languages;
    }

    //date reformating
    public String getFormattedDate() {
        if (pushedAt == null) return "";
        // Just grab the YYYY-MM-DD part for now
        return pushedAt.substring(0, 10);
    }
}
