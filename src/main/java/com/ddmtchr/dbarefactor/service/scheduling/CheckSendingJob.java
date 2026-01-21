package com.ddmtchr.dbarefactor.service.scheduling;


import com.ddmtchr.dbarefactor.service.CheckMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckSendingJob extends QuartzJobBean {

    private final CheckMessageSender checkMessageSender;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        try {
            checkMessageSender.processCheckMessages();
        } catch (Exception e) {
            log.error("CheckSendingJob failed: {}", e.getMessage());
        }
    }
}
