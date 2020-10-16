package io.spring.batch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("job")
public class JobLaunchingController {
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @RequestMapping("launchJob")
    public String handle() throws Exception {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run(job, new JobParameters());
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return "jobExecution's info: Id = " + jobExecution.getId() + " ,status = " + jobExecution.getExitStatus();
    }
}
