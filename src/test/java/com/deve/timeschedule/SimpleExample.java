/* 
 * Copyright 2005 - 2009 Terracotta, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */

package com.deve.timeschedule;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 * This Example will demonstrate how to start and shutdown the Quartz 
 * scheduler and how to schedule a job to run in Quartz.
 * 
 * @author Bill Kratzer
 */
public class SimpleExample {

    
    public void run() throws Exception {
        Logger log = Logger.getLogger(SimpleExample.class);

        log.info("------- Initializing ----------------------");

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        log.info("------- Initialization Complete -----------");

        log.info("------- Scheduling Jobs -------------------");

        // computer a time that is on the next round minute
        Date runTime = TriggerUtils.getEvenMinuteDate(new Date());

        // define the job and tie it to our HelloJob class
        JobDetail job = new JobDetail("job1", "group1", HelloJob.class);
        JobDetail job1 = new JobDetail("job2", "group1", HelloJob.class);
        // Trigger the job to run on the next round minute
        Trigger trigger = TriggerUtils.makeSecondlyTrigger();
        trigger.setName("trigger");
        
        Trigger trigger1 = TriggerUtils.makeSecondlyTrigger();
        trigger1.setName("trigger1");
           // new SimpleTrigger("trigger1", "group1", runTime);
        
        // Tell quartz to schedule the job using our trigger
        sched.scheduleJob(job, trigger);
        sched.scheduleJob(job1,trigger1);
        log.info(job.getFullName() + " will run at: " + runTime);  

        // Start up the scheduler (nothing can actually run until the 
        // scheduler has been started)
        sched.start();
        log.info("------- Started Scheduler -----------------");

        // wait long enough so that the scheduler as an opportunity to 
        // run the job!
        log.info("------- Waiting 90 seconds... -------------");
        try {
            // wait 90 seconds to show jobs
            Thread.sleep(90L * 1000L); 
            // executing...
        } catch (Exception e) {
        }

        // shut down the scheduler
        log.info("------- Shutting Down ---------------------");
        sched.shutdown(true);
        log.info("------- Shutdown Complete -----------------");
    }

    public static void main(String[] args) throws Exception {

        SimpleExample example = new SimpleExample();
        example.run();

    }

}
