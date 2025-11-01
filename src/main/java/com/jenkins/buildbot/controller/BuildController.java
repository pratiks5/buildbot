package com.jenkins.buildbot.controller;


import com.jenkins.buildbot.service.LogService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping
public class BuildController {

    private final String API_TOKEN = "115cd2114bd1495d69b8802a42c287057d";


    private ChatClient chatClient;


    private final LogService logService;

    public BuildController(ChatClient.Builder builder, LogService aiService) {
        this.chatClient = builder.build();
        this.logService = aiService;
    }

    @GetMapping("/jenkins/analyze-build")
    public ResponseEntity<String> analyzeBuild(@RequestParam String jobName, @RequestParam int buildNumber) {

        String logs = logService.getBuildLog(jobName, buildNumber);
        String analysis = chatClient.prompt(logs).call().content();

        return ResponseEntity.ok(analysis);
    }


    @GetMapping("/jenkins/build-log")
    public List<String> getBuildLog(@RequestParam String jobName, @RequestParam int buildNumber) {
        String url = JENKINS_URL + "/job/" + jobName + "/" + buildNumber + "/consoleText";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(USERNAME, API_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        String[] lines = response.getBody().split("\n");
        return Arrays.asList(lines);

    }
}
