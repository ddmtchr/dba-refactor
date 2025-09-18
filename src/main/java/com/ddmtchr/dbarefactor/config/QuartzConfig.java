package com.ddmtchr.dbarefactor.config;

import com.ddmtchr.dbarefactor.service.scheduling.BookingFinishJob;
import com.ddmtchr.dbarefactor.service.scheduling.CheckSendingJob;
import com.ddmtchr.dbarefactor.service.scheduling.PaymentTimeoutsJob;
import com.ddmtchr.dbarefactor.service.scheduling.PayoutsJob;
import org.quartz.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail paymentTimeoutsJobDetail() {
        return JobBuilder.newJob(PaymentTimeoutsJob.class)
                .withIdentity("paymentTimeoutsJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger paymentTimeoutsJobTrigger(JobDetail paymentTimeoutsJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(paymentTimeoutsJobDetail)
                .withIdentity("paymentTimeoutsTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(1)
                        .repeatForever())
                .build();
    }

    @Bean
    public JobDetail payoutsJobDetail() {
        return JobBuilder.newJob(PayoutsJob.class)
                .withIdentity("payoutsJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger payoutsJobTrigger(JobDetail payoutsJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(payoutsJobDetail)
                .withIdentity("payoutsTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(20)
                        .repeatForever())
                .build();
    }

    @Bean
    public JobDetail bookingFinishJobDetail() {
        return JobBuilder.newJob(BookingFinishJob.class)
                .withIdentity("bookingFinishJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger bookingFinishTrigger(JobDetail bookingFinishJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(bookingFinishJobDetail)
                .withIdentity("bookingFinishTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(10)
                        .repeatForever())
                .build();
    }

    @Bean
    public JobDetail checkSendingJobDetail() {
        return JobBuilder.newJob(CheckSendingJob.class)
                .withIdentity("checkSendingJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger checkSendingTrigger(JobDetail checkSendingJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(checkSendingJobDetail)
                .withIdentity("checkSendingTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(20)
                        .repeatForever())
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource quartzDataSource, ApplicationContext applicationContext,
                                                     QuartzProperties properties, ObjectProvider<JobDetail> jobDetails,
                                                     ObjectProvider<Trigger> triggers) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        if (properties.getSchedulerName() != null) {
            schedulerFactoryBean.setSchedulerName(properties.getSchedulerName());
        }
        schedulerFactoryBean.setAutoStartup(properties.isAutoStartup());
        schedulerFactoryBean.setStartupDelay((int) properties.getStartupDelay().getSeconds());
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(properties.isWaitForJobsToCompleteOnShutdown());
        schedulerFactoryBean.setOverwriteExistingJobs(properties.isOverwriteExistingJobs());
        if (!properties.getProperties().isEmpty()) {
            schedulerFactoryBean.setQuartzProperties(asProperties(properties.getProperties()));
        }
        schedulerFactoryBean.setJobDetails(jobDetails.orderedStream().toArray(JobDetail[]::new));
        schedulerFactoryBean.setTriggers(triggers.orderedStream().toArray(Trigger[]::new));

        schedulerFactoryBean.setDataSource(quartzDataSource);
        return schedulerFactoryBean;
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }
}
