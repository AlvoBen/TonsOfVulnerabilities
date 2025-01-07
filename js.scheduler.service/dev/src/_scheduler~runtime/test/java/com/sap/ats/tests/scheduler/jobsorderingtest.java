package com.sap.ats.tests.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobsComparator;

/*
 * Tests internal ordering of a jobs, queried by job execution runtime.<br/>
 * This is a JUnit test, so to run it use the command <code>japro -target=test</code>
 * @author I057508(vladislav.iliev@sap.com)
 * @since 7.20 (SP0)
 */
public class JobsOrderingTest {
	
	private static final String LINE = "-------------------------------------------------------------------------------------------------------------------------";

	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss.SSS");

	final String unsortedJobIds[][] = new String[][] {
			{ "76f44b6031a511debf66001999492810", "25.04.2009 17.29.20.633" },
			{ "778ce1e031a511debf1a001999492810", "25.04.2009 17.29.21.323" },
			{ "7767a6a031a511de8560001999492810", "25.04.2009 17.29.21.107" },
			{ "7784f2a031a511de986d001999492810", "25.04.2009 17.29.21.327" },
			{ "7770322031a511deca99001999492810", "25.04.2009 17.29.21.183" },
			{ "7786a05031a511dea092001999492810", "25.04.2009 17.29.21.323" },
			{ "7783931031a511deaa91001999492810", "25.04.2009 17.29.21.177" },
			{ "778b825131a511dea6d5001999492810", "25.04.2009 17.29.21.330" },
			{ "7781703031a511deb5d7001999492810", "25.04.2009 17.29.21.177" },
			{ "76fb9e6031a511deb926001999492810", "25.04.2009 17.29.20.633" },
			{ "777d2a7031a511dec993001999492810", "25.04.2009 17.29.21.177" },
			{ "776d730031a511dea09f001999492810", "25.04.2009 17.29.21.170" },
			{ "7769063031a511de9d10001999492810", "25.04.2009 17.29.21.140" },
			{ "776e847031a511dea21e001999492810", "25.04.2009 17.29.21.127" },
			{ "77769ac131a511deb42c001999492810", "25.04.2009 17.29.21.177" },
			{ "7764725031a511deb356001999492810", "25.04.2009 17.29.21.107" },
			{ "770d2a9031a511dea3dd001999492810", "25.04.2009 17.29.21.047" },
			{ "7762768131a511de947b001999492810", "25.04.2009 17.29.21.037" },
			{ "7773667031a511decff5001999492810", "25.04.2009 17.29.21.177" },
			{ "77049f1031a511de85db001999492810", "25.04.2009 17.29.20.657" },
			{ "76fef9c031a511de8484001999492810", "25.04.2009 17.29.20.633" },
			{ "777b2ea131a511de9e91001999492810", "25.04.2009 17.29.21.177" },
			{ "76ed468031a511de88a9001999492810", "25.04.2009 17.29.20.633" } };

	final String sortedJobIds[][] = new String[][] {
			{ "778b825131a511dea6d5001999492810", "25.04.2009 17.29.21.330" },
			{ "7784f2a031a511de986d001999492810", "25.04.2009 17.29.21.327" },
			{ "778ce1e031a511debf1a001999492810", "25.04.2009 17.29.21.323" },
			{ "7786a05031a511dea092001999492810", "25.04.2009 17.29.21.323" },
			{ "7770322031a511deca99001999492810", "25.04.2009 17.29.21.183" },
			{ "7783931031a511deaa91001999492810", "25.04.2009 17.29.21.177" },
			{ "7781703031a511deb5d7001999492810", "25.04.2009 17.29.21.177" },
			{ "777d2a7031a511dec993001999492810", "25.04.2009 17.29.21.177" },
			{ "777b2ea131a511de9e91001999492810", "25.04.2009 17.29.21.177" },
			{ "77769ac131a511deb42c001999492810", "25.04.2009 17.29.21.177" },
			{ "7773667031a511decff5001999492810", "25.04.2009 17.29.21.177" },
			{ "776d730031a511dea09f001999492810", "25.04.2009 17.29.21.170" },
			{ "7769063031a511de9d10001999492810", "25.04.2009 17.29.21.140" },
			{ "776e847031a511dea21e001999492810", "25.04.2009 17.29.21.127" },
			{ "7767a6a031a511de8560001999492810", "25.04.2009 17.29.21.107" },
			{ "7764725031a511deb356001999492810", "25.04.2009 17.29.21.107" },
			{ "770d2a9031a511dea3dd001999492810", "25.04.2009 17.29.21.047" },
			{ "7762768131a511de947b001999492810", "25.04.2009 17.29.21.037" },
			{ "77049f1031a511de85db001999492810", "25.04.2009 17.29.20.657" },
			{ "76fef9c031a511de8484001999492810", "25.04.2009 17.29.20.633" },
			{ "76fb9e6031a511deb926001999492810", "25.04.2009 17.29.20.633" },
			{ "76f44b6031a511debf66001999492810", "25.04.2009 17.29.20.633" },
			{ "76ed468031a511de88a9001999492810", "25.04.2009 17.29.20.633" } };
	
	private List<Job> sortedByTheSchedulerJobs = new ArrayList<Job>(
			unsortedJobIds.length);

	private void loadJobArrays() throws Exception {
		try {
			for (int i = 0; i < unsortedJobIds.length; i++) {
				JobID jobId = JobID.parseID(unsortedJobIds[i][0]);
				Date endDate = sdf.parse(unsortedJobIds[i][1]);
			
				//create dummy job
				Job job = new Job(jobId, null, null, null, null, null, endDate,
						null, null, (short) 0, null, null, null, false, 0, null);
				
				sortedByTheSchedulerJobs.add(job);
			}
		} catch (Exception e) {
			throw new Exception(
					"Error loading jobs array. The test will fail! Exception:\n "
							+ e);
		}

	}
	private void sortJobs() {
		//get jobs runtime comparator
		JobsComparator schedulerComparator = new JobsComparator(false);
		//sort
		Collections.sort(sortedByTheSchedulerJobs, schedulerComparator);			
	}
	
	private void compareJobs() {
		
		Assert.assertEquals(unsortedJobIds.length, sortedJobIds.length);//to avoid test developer's mistake
		Assert.assertEquals(sortedJobIds.length, sortedByTheSchedulerJobs.size());

		printJobs();
		
		for (int i = 0; i < sortedJobIds.length; i++) {
			Assert.assertNotNull(sortedJobIds[i][0]);//to avoid test developer's mistake
			Assert.assertNotNull(sortedByTheSchedulerJobs.get(i));//if the runtime has a bug
			Assert.assertNotNull(sortedByTheSchedulerJobs.get(i).getId());//if runtime has a bug
			
			Assert.assertEquals(sortedJobIds[i][0].toString(),sortedByTheSchedulerJobs.get(i).getId().toString());
		}
		System.out.println("Test Finished Successfully.");
	}

	private void printJobs() {
		System.out.println(LINE);
		System.out.println("- Expected order of jobs                                   *    Actual order                                            -");
		System.out.println(LINE);
		
		for (int i = 0; i < sortedJobIds.length; i++) {
			System.out.println(sortedJobIds[i][0] + " " + sortedJobIds[i][1] + 
					"\t" + sortedByTheSchedulerJobs.get(i).getId() + " " +
					sdf.format(sortedByTheSchedulerJobs.get(i).getEndDate()) + "\n");
		}
		System.out.println(LINE);
	}
	@Test
	public void testJobsComparator() throws Exception {
		loadJobArrays();
		sortJobs();
		compareJobs();		
	}
}
