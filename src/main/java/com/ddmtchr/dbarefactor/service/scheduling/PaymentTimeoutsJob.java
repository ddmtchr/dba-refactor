package com.ddmtchr.dbarefactor.service.scheduling;

import com.ddmtchr.dbarefactor.service.PaymentTimeoutService;
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
public class PaymentTimeoutsJob extends QuartzJobBean {

    private final PaymentTimeoutService paymentTimeoutService;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        log.info("----- Job PaymentTimeoutsJob started -----");
        this.paymentTimeoutService.processPaymentTimeout();
        log.info("----- Job PaymentTimeoutsJob finished -----");
    }
}
