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

import com.deve.timeschedule.CoreJob;
import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;

public class HelloJob extends CoreJob {

	private static final Log _log = LogFactory.getLog(HelloJob.class);

	public HelloJob() {
	}

	@Override
	public void run() {
		// Say Hello to the World and display the date/time
		_log.info(getJobName() + " Job started at - "
				+ Thread.currentThread().getId() + "@" + new Date());
		final String jobName = getJobName();
		
		_log.info(getJobName() + " Job Finished at - "
				+ Thread.currentThread().getId() + "@" + new Date());
	}

}
