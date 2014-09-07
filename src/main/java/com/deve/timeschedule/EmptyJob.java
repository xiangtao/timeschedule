package com.deve.timeschedule;

import java.util.Date;

import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;

public class EmptyJob extends CoreJob {

	private static final Log _log = LogFactory.getLog(EmptyJob.class);

	public EmptyJob() {
	}

	@Override
	public void run() {
		_log.info(getJobName() + " Job started at - " + Thread.currentThread().getId() + "@" + new Date());
	}
}
