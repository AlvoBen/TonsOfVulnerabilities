package com.sap.scheduler.runtime;

import java.util.Comparator;


/**
 * Compare 2 jobs.<br/>
 * There is already compare functionality of a jobs<br/>
 * but only by ascending order of the ids. There is a parameter called <i>order</i>, <br/>
 * which is responsible for type of ordering of the jobs.<br/>
 * <ol>
 * <li>Compare by end dates. The comparison is the same(<i>orderAsc</i> is not taken into account)
 * <li>If the jobs are equal, compare be job id. The id is a byte array and GUID is used to compare them.<br/>
 * Here the <i>orderAsc</i> is applied. 
 * </ol>
 * @author I057508 (vladislav.iliev@sap.com)
 */
public class JobsComparator implements Comparator<Job> {
	boolean orderAsc = true;
	/**
	 * Instantiates Jobs comparator
	 * @param orderAsc true for ascending , false for descending order (order is applied only on ids)
	 */
	public JobsComparator(boolean orderAsc) {
		this.orderAsc = orderAsc;
	}
	
	@Override
	public int compare(Job j1, Job j2) {
		if (orderAsc) {
			int res = Job.compareEndDates(j1, j2);
			
			if (res != 0) {
				return res;
			}
			// equal - > compare by id
			return Job.compareIds(j1, j2);	
			
		} else {
			
			int res = Job.compareEndDates(j2, j1);
			
			if (res != 0) {
				return res;
			}
			// equal - > compare by id
			return Job.compareIds(j2, j1);	
		}
	}		    	
}