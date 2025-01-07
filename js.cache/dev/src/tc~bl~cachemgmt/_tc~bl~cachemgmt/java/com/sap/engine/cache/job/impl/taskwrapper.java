/*
 * Created on 2004.9.15
 *
 */
package com.sap.engine.cache.job.impl;

import com.sap.engine.cache.job.Task;

/**
 * @author petio-p
 *
 */
public class TaskWrapper implements Task {

  static final long serialVersionUID = 6564701865767839803L;
  
  int queuePosition = -1;
  long nextCallTime = -1;
  private transient BackgroundExactImpl background = null;
  
  public TaskWrapper(Task aggregate, BackgroundExactImpl background) {
    this.aggregate = aggregate;
    this.background = background;
  }
  
  Task aggregate = null;
	public boolean equals(Object obj) { return aggregate.equals(obj); }
	public int getInterval() { return aggregate.getInterval(); }
	public String getName() { return aggregate.getName(); }
	public byte getScope() {return aggregate.getScope(); }
	public int hashCode() { return aggregate.hashCode(); }
	public boolean repeatable() { return aggregate.repeatable(); }
	public String toString() { return aggregate.toString(); }

  public void run() { 
    try {
      aggregate.run(); 
    } finally {
      background.processNode(this);
    }
  }

}
