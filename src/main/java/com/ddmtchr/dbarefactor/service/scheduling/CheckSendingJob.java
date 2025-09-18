package com.ddmtchr.dbarefactor.service.scheduling;


import com.ddmtchr.dbarefactor.service.CheckMessageSender;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckSendingJob extends QuartzJobBean {

    private final CheckMessageSender checkMessageSender;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        this.checkMessageSender.processCheckMessages();
    }
}
