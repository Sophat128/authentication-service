package com.example.custom;

import com.example.service.impl.ScheduledJob;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.stereotype.Component;

import org.quartz.spi.JobFactory;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Component
public class CDIJobFactory implements JobFactory {

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        try {
            InitialContext initialContext = new InitialContext();
            Bean<?> bean = CDI.current().getBeanManager().getBeans(bundle.getJobDetail().getJobClass()).iterator().next();
            return (Job) CDI.current().select(bean.getBeanClass()).get();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}


