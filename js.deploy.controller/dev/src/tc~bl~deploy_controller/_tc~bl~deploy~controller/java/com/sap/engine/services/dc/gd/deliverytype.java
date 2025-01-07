package com.sap.engine.services.dc.gd;

import java.util.HashMap;
import java.util.Map;

public final class DeliveryType {

	public static final DeliveryType NORMAL = new DeliveryType(new Integer(0),
			"normal");

	public static final DeliveryType ROLLING = new DeliveryType(new Integer(1),
			"rolling");

	private static final Map TYPE_MAP = new HashMap();

	private final Integer id;
	private final String name;
	private final String toString;

	static {
		TYPE_MAP.put(NORMAL.getName(), NORMAL);
		TYPE_MAP.put(ROLLING.getName(), ROLLING);
	}

	public static DeliveryType getDeliveryTypeByName(String name) {
		return (DeliveryType) TYPE_MAP.get(name);
	}

	public static Map getNameAndDeployWorkflowStrategy() {
		return TYPE_MAP;
	}

	private DeliveryType(Integer id, String name) {
		this.id = id;
		this.name = name;
		this.toString = name + " delivery type";
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DeliveryType)) {
			return false;
		}

		DeliveryType other = (DeliveryType) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
