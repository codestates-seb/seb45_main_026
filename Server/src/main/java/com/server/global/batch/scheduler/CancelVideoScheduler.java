package com.server.global.batch.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CancelVideoScheduler {

    private final Job cancelVideoJob;
    private final JobLauncher jobLauncher;

    public CancelVideoScheduler(@Qualifier("cancelVideoJob") Job cancelVideoJob,
                                JobLauncher jobLauncher) {
        this.cancelVideoJob = cancelVideoJob;
        this.jobLauncher = jobLauncher;
    }

    //매일 새벽 1시에 실행
    @Scheduled(cron = "0 0 1 * * *")
    public void executeJob() {
        try {
            jobLauncher.run(cancelVideoJob,
                    new JobParametersBuilder()
                            .addString("time", LocalDateTime.now().toString())
                            .toJobParameters() //Job 실행 시 필요한 파라미터 설정
            );
        } catch (JobExecutionException e) {
            log.error(e.getMessage() + " 으로 인한 비디오 배치작업 실패");
            e.printStackTrace();
        }
    }
}
