package com.sap.sdo.testcase.internal.pojo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface IOrder {

	public final static short TRANSIENT = 0; // not submitted (never on DB)

	public final static short SUBMITTED = 1; // submitted

	public final static short CONFIRMED = 2; // confirmed (will be shipped)

	public final static short COMPLETED = 3; // shipped and paid

	public final static short CANCELLED = 4; // cancelled 

	public final static short CLOSED = 5; // to be archived

	public abstract Date getCancelled();

	public abstract void setCancelled(Date cancelled);

	public abstract Date getClosed();

	public abstract void setClosed(Date closed);

	public abstract Date getConfirmed();

	public abstract void setConfirmed(Date confirmed);

	public abstract long getId();

	public abstract void setId(long id);

	@SuppressWarnings("unchecked")
	@SdoPropertyMetaData(opposite = "order")
	public abstract List<POOrderItem> getLineItems();

	@SuppressWarnings("unchecked")
	public abstract void setLineItems(List<POOrderItem> lineItems);

	public abstract short getStatus();

	public abstract void setStatus(short status);

	public abstract Date getSubmitted();

	public abstract void setSubmitted(Date submitted);

	public abstract Date getCompleted();

	public abstract void setCompleted(Date completed);

	public abstract String getUser();

	public abstract void setUser(String user);

	public abstract BigDecimal getTotal();

	public abstract void setTotal(BigDecimal total);

}