package com.sap.engine.core.session;

import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.thread.ThreadManager;
import com.sap.engine.session.exec.ThreadContextProxy;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.usr.ThrLocalContainer;

public class ThreadContextProxyImpl implements ThreadContextProxy {

	ThreadManager thrManager;

	private ThreadManager threadManager() {
		if (thrManager == null) {
			synchronized (this) {
				if (thrManager == null) {
					thrManager = ((ThreadManager) Framework.getManager(Names.APPLICATION_THREAD_MANAGER));
				}
			}
		}
		return thrManager;
	}

	public ThrLocalContainer getCurrentSecContext() {
		return (ThrLocalContainer) threadManager().getThreadContext().getContextObject(
				ThrLocalContainer.THREAD_LOCAL_KEY);

	}

  public String getTenancyID() {
		SessionExecContext tc = (SessionExecContext) threadManager().getThreadContext().getContextObject(
				SessionExecContext.THREAD_CONTEXT_OBJECT_NAME);
		if (tc != null) {
			return tc.getTenancyID();
		}
		return null;
	}

	public void setTenancyID(String id) {
		SessionExecContext tc = (SessionExecContext) threadManager().getThreadContext().getContextObject(
				SessionExecContext.THREAD_CONTEXT_OBJECT_NAME);
		if (tc != null) {
			tc.setTenancyID(id);
		}
	}

	public void sheduleSession(RuntimeSessionModel session) {
		SessionExecContext tc = (SessionExecContext) threadManager().getThreadContext().getContextObject(
				SessionExecContext.THREAD_CONTEXT_OBJECT_NAME);
		if (tc != null) {
			tc.shedule(session);
		}
	}

  public SessionExecContext currentContextObject() {
    return (SessionExecContext) threadManager().getThreadContext().getContextObject(SessionExecContext.THREAD_CONTEXT_OBJECT_NAME);
  }

	public void sheduleSessionPassivation(RuntimeSessionModel session) {
		SessionExecContext tc = (SessionExecContext) threadManager().getThreadContext().getContextObject(
				SessionExecContext.THREAD_CONTEXT_OBJECT_NAME);
		if (tc != null) {
			tc.shedule(session);
		}
	}




}