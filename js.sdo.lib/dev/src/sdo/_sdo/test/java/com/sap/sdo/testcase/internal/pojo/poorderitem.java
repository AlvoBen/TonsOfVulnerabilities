package com.sap.sdo.testcase.internal.pojo;

import java.math.BigDecimal;


import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;

@SdoTypeMetaData(uri="http://www.sample.org/shop/", sdoName="OrderItem")
public class POOrderItem {

	private long id;

	private String matnr;

	private int quantity;

	private String description;

	private IOrder order;

	private BigDecimal price;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMatnr() {
		return matnr;
	}
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
    @SdoPropertyMetaData(opposite="lineItems")
	public IOrder getOrder() {
		return order;
	}
	public void setOrder(IOrder order) {
		this.order = order;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
