package com.server.global.batch.cancelvideo;

import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class CancelVideoJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final AwsService awsService;
    private final VideoRepository videoRepository;

    public CancelVideoJobConfig(JobBuilderFactory jobBuilderFactory,
                                StepBuilderFactory stepBuilderFactory,
                                EntityManagerFactory entityManagerFactory,
                                AwsService awsService,
                                VideoRepository videoRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.awsService = awsService;
        this.videoRepository = videoRepository;
    }

    @Bean(name = "cancelVideoJob")
    public Job cancelVideoJob() throws Exception {
        return jobBuilderFactory.get("cancelVideoJob")
                .start(cancelVideoStep())
                .build();
    }

    @Bean
    @JobScope
    public Step cancelVideoStep() throws Exception {
        return stepBuilderFactory.get("cancelVideoStep")
                .<Video, Video>chunk(100)
                .reader(videoReader())
                .processor(videoProcessor())
                .writer(videoDeletionWriter())
                .build();
    }

    // Reader 설정 - 데이터베이스로부터 특정 조건에 맞는 데이터를 읽어오는 역할
    @Bean
    @StepScope
    public JpaPagingItemReader<Video> videoReader() throws Exception {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("thirtyMinutesAgo", LocalDateTime.now().minusMinutes(30));

        return new JpaPagingItemReaderBuilder<Video>()
                .name("JpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM Video v " +
                        "WHERE v.videoStatus = 'UPLOADING' " +
                        "AND v.createdDate <= :thirtyMinutesAgo " +
                        "ORDER BY v.videoId ASC")
                .parameterValues(parameterValues)
                .pageSize(1000) // 페이징 사이즈를 1000 으로 설정 -> 누락되는거 없이 한번에 들고오자는 생각
                .build();
    }

    // Processor 설정 - 읽어온 데이터를 기반으로 비즈니스 로직을 수행하는 역할
    @Bean
    @StepScope
    public ItemProcessor<Video, Video> videoProcessor() {
        return video -> {
            if(awsService.isExistFile(video.getVideoFile(), FileType.VIDEO)) {
                awsService.deleteFile(video.getVideoFile(), FileType.VIDEO);
            }
            if(awsService.isExistFile(video.getThumbnailFile(), FileType.THUMBNAIL)) {
                awsService.deleteFile(video.getThumbnailFile(), FileType.THUMBNAIL);
            }
            if(awsService.isExistFile(video.getPreviewFile(), FileType.PREVIEW)) {
                awsService.deleteFile(video.getPreviewFile(), FileType.PREVIEW);
            }
            return video;
        };
    }

    // Writer 설정 - 처리된 결과 데이터를 데이터베이스에 저장하는 역할
    @Bean
    @StepScope
    public ItemWriter<Video> videoDeletionWriter() {


        return items -> videoRepository.deleteAllInBatch((List<Video>) items);
    }
}
