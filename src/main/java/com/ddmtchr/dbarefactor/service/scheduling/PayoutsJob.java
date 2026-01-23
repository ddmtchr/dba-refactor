package com.ddmtchr.dbarefactor.service.scheduling;

import com.ddmtchr.dbarefactor.service.PayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PayoutsJob extends QuartzJobBean {

    private final PayoutService payoutService;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        log.info("----- Job PayoutsJob started -----");
        this.payoutService.processPayouts();
        log.info("----- Job PayoutsJob finished -----");
    }
}
