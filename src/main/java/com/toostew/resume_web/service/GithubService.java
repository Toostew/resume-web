package com.toostew.resume_web.service;


import com.toostew.resume_web.dto.RepoReturnObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GithubService {

    final private RestClient restClient;

    public GithubService() {
        this.restClient = RestClient.create("https://api.github.com");
    }

    // "repos" is the name of the cache bucket
    // key = "#username" caches are separated by username
    @Cacheable(value = "repos", key = "'my_github_projects'")
    public List<RepoReturnObject> getRecentRepos() {
        return restClient.get()
                .uri("/users/Toostew/repos?sort=updated&per_page=4")
                .retrieve()
                // This ensures you get a List of your DTOs, not raw Maps
                .body(new ParameterizedTypeReference<List<RepoReturnObject>>() {});
    }


    public Map<String, Integer> getRepoLanguages(String repo){
        String uri = "/repos/Toostew/" + repo + "/languages";
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Integer>>() {});
    }

    public List<RepoReturnObject> getCombinedData() {
        List<RepoReturnObject> repos = getRecentRepos();
        for (RepoReturnObject repo : repos) {
            // Fetch languages using the repo name and set it in the DTO
            Map<String, Integer> langs = getRepoLanguages(repo.getName());
            repo.setLanguages(langs);
        }
        return repos;
    }





}
