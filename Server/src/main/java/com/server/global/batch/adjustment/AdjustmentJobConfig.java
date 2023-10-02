package com.server.global.batch.adjustment;

import com.server.domain.adjustment.domain.Adjustment;
import com.server.domain.adjustment.repository.AdjustmentRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.module.firmbank.FirmBankService;
import com.server.module.firmbank.response.AdjustmentResult;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
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
public class AdjustmentJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final AdjustmentRepository adjustmentRepository;
    private final FirmBankService firmBankService;

    public AdjustmentJobConfig(JobBuilderFactory jobBuilderFactory,
                               StepBuilderFactory stepBuilderFactory,
                               EntityManagerFactory entityManagerFactory, AdjustmentRepository adjustmentRepository, FirmBankService firmBankService) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.adjustmentRepository = adjustmentRepository;
        this.firmBankService = firmBankService;
    }

    @Bean(name = "adjustmentJob")
    public Job adjustmentJob() throws Exception {
        return jobBuilderFactory.get("adjustmentJob")
                .start(adjustmentStep())
                .build();
    }

    @Bean
    @JobScope
    public Step adjustmentStep() {
        return stepBuilderFactory.get("adjustmentStep")
                .<Member, Adjustment>chunk(1000)
                .reader(memberReader())
                .processor(adjustmentProcessor())
                .writer(adjustmentWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Member> memberReader()  {

        return new JpaPagingItemReaderBuilder<Member>()
                .name("JpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT m FROM Member m " +
                        "JOIN FETCH m.account a")
                .pageSize(1000) // 페이징 사이즈를 1000 으로 설정 -> 누락되는거 없이 한번에 들고오자는 생각
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Member, Adjustment> adjustmentProcessor() {

        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        int year = monthAgo.getYear();
        int month = monthAgo.getMonthValue();

        return member -> {

            Integer amount = adjustmentRepository.calculateAmount(member.getMemberId(), month, year);
            if(amount == null || amount == 0) return null;

            AdjustmentResult result = firmBankService.adjustment(member.getAccount().getName(),
                    member.getAccount().getAccount(),
                    member.getAccount().getBank(),
                    amount);

            return Adjustment.createAdjustment(year, month, member, amount, result.getStatus(), result.getReason());
        };
    }

    // Writer 설정 - 처리된 결과 데이터를 데이터베이스에 저장하는 역할
    @Bean
    @StepScope
    public ItemWriter<Adjustment> adjustmentWriter() {

        return adjustments -> adjustmentRepository.saveAll((List<Adjustment>) adjustments);
    }
}
