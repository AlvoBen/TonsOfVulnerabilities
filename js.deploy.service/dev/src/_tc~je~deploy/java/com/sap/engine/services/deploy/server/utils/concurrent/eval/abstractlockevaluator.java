package com.sap.engine.services.deploy.server.utils.concurrent.eval;

import java.util.Collection;

import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator;
import com.sap.engine.services.deploy.server.utils.concurrent.EnqueueLocker.EnqueueLock;

/**
 * Abstract lock evaluator.
 * @author Emil Dinchev
 */
public abstract class AbstractLockEvaluator 
	implements LockEvaluator<Component> {

	private final String operation;
	private final Component root;
	private final Status targetStatus;
	private final long timeout;
	private final EnqueueLock enqueueLock;
	
	/**
	 * @param operation not null.
	 * @param targetStatus not null.
	 * @param root not null.
	 * @param enqueueLockType enqueue lock type.
	 * @param timeout timeout in milliseconds.
	 */
	protected AbstractLockEvaluator(final String operation,
		final Status targetStatus, final Component root, 
		final char enqueueLockType, final long timeout) {
		assert operation != null;
		assert targetStatus != null;
		assert root != null;
		
		this.operation = operation;
		this.targetStatus = targetStatus;
		this.root = root;
		this.timeout = timeout;
		enqueueLock = new EnqueueLock(root.toString(), enqueueLockType);
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator#getOperation()
	 */
	public String getOperation() {
		return operation;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator#getEnqueueLock()
	 */
	public EnqueueLock getEnqueueLock() {
		return enqueueLock;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator#getRoot()
	 */
	public Component getRoot() {
		return root;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator#getTimeout()
	 */
	public long getTimeout() {
		return timeout;
	}
	
	/* (non-Javadoc)
	 * @see 
	 * com.sap.engine.services.deploy.server.utils.concurrent.LockEvaluator#
	 *  evaluate()
	 */
	public abstract Collection<LockEntry<Component>> evaluate();
	
	/**
	 * @param component
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected boolean isStatusDifferent(Component component) {
		return !targetStatus.equals(getStatus(component));
	}
	
	/**
	 * If this is not a deployed application, then the target status will be 
	 * returned. 
	 * @param component
	 * @return status - not null.
	 */
	protected Status getStatus(Component component) {
		if(component.getType() != Component.Type.APPLICATION) {
			return targetStatus;
		}
		final DeploymentInfo info = Applications.get(component.getName());
		return (info == null) ? targetStatus : info.getStatus();
	}
}