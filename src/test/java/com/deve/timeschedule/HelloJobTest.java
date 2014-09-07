package com.deve.timeschedule;

import org.junit.Before;
import org.junit.Test;

public class HelloJobTest {
	
	HelloJob job = null;	
	
	@Before
	public void pre(){
		job = new HelloJob();
	}
	
	@Test
	public void testRun(){
		job.run();
	}

}
