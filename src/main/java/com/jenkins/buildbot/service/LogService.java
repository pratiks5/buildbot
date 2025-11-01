package com.jenkins.buildbot.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class LogService {

    public String fetchJenkinsLogs(String jobName, int buildNumber) {
        String fileName = "jenkinsLogs.txt";
        StringBuilder content = new StringBuilder();

        try {
            ClassPathResource resource = new ClassPathResource("static/" + fileName);
            InputStream inputStream = resource.getInputStream();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
        return prompt+content.toString();
    }



    String prompt = """
            You are an expert Jenkins CI/CD troubleshooter analyzing build logs.
            
            Analyze the following Jenkins build log and respond in strict JSON format:
            {
              "status": "SUCCESS|FAILURE|UNSTABLE",
              "stage": "<pipeline stage where issue occurred>",
              "error_category": "<BUILD|TEST|DEPLOY|ENVIRONMENT|CODE|INFRASTRUCTURE>",
              "error_summary": "<brief technical error description>",
              "root_cause": "<most likely underlying cause>",
              "recommendation": "<specific actionable fix>",
              "severity": "CRITICAL|HIGH|MEDIUM|LOW",
              "next_steps": ["<step1>", "<step2>", "<step3>"]
            }
            
            Focus on identifying:
            - Specific error patterns (exceptions, exit codes, failure keywords)
            - Pipeline stage context (build, test, deploy phases)
            - Error classification (dependency, syntax, environment, network, permission issues)
            - Concrete remediation steps based on common CI/CD failure patterns
            
            Build log follows :
            """;

}
