package com.sap.sdo.testcase.internal.pojo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;


@SdoTypeMetaData(uri="http://www.sample.org/shop/", sdoName="Order")
public class POOrder implements IOrder {
	private long id;

	private Date cancelled;

	private Date confirmed;

	private Date completed;

	private Date submitted;

	private Date closed;

	private short status = TRANSIENT;

	private String user;

	private BigDecimal total;

	private List<POOrderItem> lineItems;

	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getCancelled()
	 */
	public Date getCancelled() {
		return cancelled;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setCancelled(java.util.Date)
	 */
	public void setCancelled(Date cancelled) {
		this.cancelled = cancelled;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getClosed()
	 */
	public Date getClosed() {
		return closed;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setClosed(java.util.Date)
	 */
	public void setClosed(Date closed) {
		this.closed = closed;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getConfirmed()
	 */
	public Date getConfirmed() {
		return confirmed;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setConfirmed(java.util.Date)
	 */
	public void setConfirmed(Date confirmed) {
		this.confirmed = confirmed;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getId()
	 */
	public long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getLineItems()
	 */
	@SuppressWarnings("unchecked")
	@SdoPropertyMetaData(opposite="order")
	public List<POOrderItem> getLineItems() {
		return (List) lineItems;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setLineItems(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public void setLineItems(List<POOrderItem> lineItems) {
		this.lineItems = (List) lineItems;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getStatus()
	 */
	public short getStatus() {
		return status;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setStatus(short)
	 */
	public void setStatus(short status) {
		// local logic 
		// in SDO case move to update method in service
		if (this.status==status) {
			return;
		}
		switch (status) {
		case SUBMITTED:
			this.submitted = new Date();
			break;
		case CONFIRMED:
			this.confirmed = new Date();
			break;
		case CANCELLED:
			this.cancelled = new Date();
			break;
		case COMPLETED:
			this.completed = new Date();
			break;
		case CLOSED:
			this.closed = new Date();
			break;
		}
		this.status = status;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getSubmitted()
	 */
	public Date getSubmitted() {
		return submitted;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setSubmitted(java.util.Date)
	 */
	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getCompleted()
	 */
	public Date getCompleted() {
		return completed;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setCompleted(java.util.Date)
	 */
	public void setCompleted(Date completed) {
		this.completed = completed;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getUser()
	 */
	public String getUser() {
		return user;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setUser(java.lang.String)
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#getTotal()
	 */
	public BigDecimal getTotal() {
		return total;
	}
	/* (non-Javadoc)
	 * @see com.sap.sdo.testcase.internal.pojo.IOrder#setTotal(java.math.BigDecimal)
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}


}
