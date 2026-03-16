package com.toostew.resume_web.service;


import com.toostew.resume_web.dto.RepoReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GithubService {

    final private RestClient restClient;



    //we are essentially going to call ourself
    //if we call @cacheable methods directly without going through a class, it actually bypasses the
    //caching altogether.
    //by providing a seperate GithubService class we can route our data through, we are technically not calling ourselves
    //hence, we dont bypass the cache. pretty dumb and smart
    @Autowired
    @Lazy
    private GithubService self;

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



    //the value is actually the name of the cache we want to use. Used with key in the key-value pair
    //we can capture the argument being passed into the method to use in the key
    //notice how #repo and the argument repo are actually the same thing
    //so essentially we are storing the value for each key
    @Cacheable(value = "repoLanguages", key = "#repo")
    public Map<String, Integer> getRepoLanguages(String repo){
        String uri = "/repos/Toostew/" + repo + "/languages";
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Integer>>() {});
    }

    public List<RepoReturnObject> getCombinedData() {
        List<RepoReturnObject> repos = self.getRecentRepos();
        for (RepoReturnObject repo : repos) {
            // Fetch languages using the repo name and set it in the DTO
            Map<String, Integer> langs = self.getRepoLanguages(repo.getName());
            repo.setLanguages(langs);
        }
        return repos;
    }





}
