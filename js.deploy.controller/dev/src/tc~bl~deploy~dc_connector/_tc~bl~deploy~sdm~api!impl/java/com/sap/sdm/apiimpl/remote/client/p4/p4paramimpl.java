package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.Param;
import com.sap.sdm.api.remote.ParamType;
import com.sap.sdm.api.remote.ParamTypes;
import com.sap.sdm.api.remote.RemoteException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-15
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class P4ParamImpl implements Param {

	private final static int OFFSET = 17;
	private final static int MULTIPLIER = 59;

	private final String name;
	private Object value;
	private final String displayName;
	private ParamType type;
	private final boolean valueShallBeHidden;

	private String parameterValueString = null;
	private Boolean parameterValueBoolean = null;
	private Byte parameterValueByte = null;
	private Short parameterValueShort = null;
	private Integer parameterValueInt = null;
	private Long parameterValueLong = null;
	private Float parameterValueFloat = null;
	private Double parameterValueDouble = null;

	P4ParamImpl(String name, Object value) throws RemoteException {
		this(P4HelperFactoryImpl.getInstance().createType(ParamTypes.STRING),
				name, name, value, false);
	}

	P4ParamImpl(ParamType type, String name, Object value, boolean shallBeHidden)
			throws RemoteException {
		this(type, name, name, value, shallBeHidden);
	}

	P4ParamImpl(ParamType type, String name, String displayName, Object value,
			boolean shallBeHidden) throws RemoteException {
		this.name = name;
		this.value = value;
		this.displayName = displayName;
		this.type = type;
		this.valueShallBeHidden = shallBeHidden;

		checkValue();
	}

	private void checkValue() throws RemoteException {
		try {
			switch (type.getTypeAsInt()) {
			case ParamTypes.BOOLEAN:
				if (null != value) {
					this.parameterValueBoolean = (Boolean) value;
				}
				break;
			case ParamTypes.BYTE:
				if (null != value) {
					this.parameterValueByte = (Byte) value;
				}
				break;
			case ParamTypes.DOUBLE:
				if (null != value) {
					this.parameterValueDouble = (Double) value;
				}
				break;
			case ParamTypes.FLOAT:
				if (null != value) {
					this.parameterValueFloat = (Float) value;
				}
				break;
			case ParamTypes.INT:
				if (null != value) {
					this.parameterValueInt = (Integer) value;
				}
				break;
			case ParamTypes.LONG:
				if (null != value) {
					this.parameterValueLong = (Long) value;
				}
				break;
			case ParamTypes.SHORT:
				if (null != value) {
					this.parameterValueShort = (Short) value;
				}
				break;
			case ParamTypes.STRING:
				if (null != value) {
					this.parameterValueString = (String) value;
				}
				break;
			default:
				String errText = "Type " + type.getTypeAsString()
						+ " is unknown.";
				throw new IllegalArgumentException(errText);
			}
		} catch (ClassCastException cce) {
			String errText = "Value or DefaultValue do not match type declaration.";
			throw new IllegalArgumentException(errText);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getName()
	 */
	public String getName() throws RemoteException {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getDisplayName()
	 */
	public String getDisplayName() throws RemoteException {
		return this.displayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getType()
	 */
	public ParamType getType() throws RemoteException {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueObject()
	 */
	public Object getValueObject() throws RemoteException {
		return this.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#setValue(java.lang.Object)
	 */
	public void setValue(Object paramValue) throws RemoteException {
		this.value = paramValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#isValueSet()
	 */
	public boolean isValueSet() throws RemoteException {
		try {
			switch (this.type.getTypeAsInt()) {
			case ParamTypes.BOOLEAN:
				boolean dummyBoolean = this.getValueBoolean();
				return true;
			case ParamTypes.BYTE:
				byte dummyByte = this.getValueByte();
				return true;
			case ParamTypes.DOUBLE:
				double dummyDouble = this.getValueDouble();
				return true;
			case ParamTypes.FLOAT:
				float dummyFloat = this.getValueFloat();
				return true;
			case ParamTypes.INT:
				int dummyInt = this.getValueInt();
				return true;
			case ParamTypes.LONG:
				long dummyLong = this.getValueLong();
				return true;
			case ParamTypes.SHORT:
				short dummyShort = this.getValueShort();
				return true;
			case ParamTypes.STRING:
				String dummyString = this.getValueString();
				return true;
			default:
				return false;
			}
		} catch (IllegalStateException ise) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#valueShallBeHidden()
	 */
	public boolean valueShallBeHidden() throws RemoteException {
		return this.valueShallBeHidden;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueString()
	 */
	public String getValueString() throws RemoteException {
		if (this.type.getTypeAsInt() == ParamTypes.STRING) {
			if (null != this.parameterValueString) {
				return this.parameterValueString;
			} else {
				throw new IllegalStateException(
						"Param has no value - call isValueSet() to check");
			}
		} else {
			throw new IllegalStateException("Config Paramater stores a "
					+ ((null == this.type) ? "null" : this.type
							.getTypeAsString()) + " type parameter, not a "
					+ ParamTypes.STRING_S + ".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueBoolean()
	 */
	public boolean getValueBoolean() throws RemoteException {
		if (this.type.getTypeAsInt() == ParamTypes.BOOLEAN) {
			if (null != this.parameterValueBoolean) {
				return this.parameterValueBoolean.booleanValue();
			} else {
				throw new IllegalStateException(
						"Param has no value - call isValueSet() to check");
			}
		} else {
			throw new IllegalStateException("Configuration Paramater stores a "
					+ this.type.getTypeAsString() + " type parameter, not a "
					+ ParamTypes.BOOLEAN_S + ".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueByte()
	 */
	public byte getValueByte() throws RemoteException {
		if (this.type.getTypeAsInt() == ParamTypes.BYTE) {
			if (null != this.parameterValueByte) {
				return this.parameterValueByte.byteValue();
			} else {
				throw new IllegalStateException(
						"Param has no value - call isValueSet() to check");
			}
		} else {
			throw new IllegalStateException("Configuration Paramater stores a "
					+ this.type.getTypeAsString() + " type parameter, not a "
					+ ParamTypes.BYTE_S + ".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueShort()
	 */
	public short getValueShort() throws RemoteException {
		if (this.type.getTypeAsInt() == ParamTypes.SHORT) {
			if (null != this.parameterValueShort) {
				return this.parameterValueShort.shortValue();
			} else {
				throw new IllegalStateException(
						"Param has no value - call isValueSet() to check");
			}
		} else {
			throw new IllegalStateException("Configuration Paramater stores a "
					+ this.type.getTypeAsString() + " type parameter, not a "
					+ ParamTypes.SHORT_S + ".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueInt()
	 */
	public int getValueInt() throws RemoteException {
		if (this.type.getTypeAsInt() == ParamTypes.INT) {
			if (null != this.parameterValueInt) {
				return this.parameterValueInt.intValue();
			} else {
				throw new IllegalStateException(
						"Param has no value - call isValueSet() to check");
			}
		} else {
			throw new IllegalStateException("Configuration Paramater stores a "
					+ this.type.getTypeAsString() + " type parameter, not a "
					+ ParamTypes.INT_S + ".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueLong()
	 */
	public long getValueLong() throws RemoteException {
		if (this.type.getTypeAsInt() == ParamTypes.LONG) {
			if (null != this.parameterValueLong) {
				return this.parameterValueLong.longValue();
			} else {
				throw new IllegalStateException(
						"Param has no value - call isValueSet() to check");
			}
		} else {
			throw new IllegalStateException("Configuration Paramater stores a "
					+ this.type.getTypeAsString() + " type parameter, not a "
					+ ParamTypes.LONG_S + ".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueFloat()
	 */
	public float getValueFloat() throws RemoteException {
		if (this.type.getTypeAsInt() == ParamTypes.FLOAT) {
			if (null != this.parameterValueFloat) {
				return this.parameterValueFloat.floatValue();
			} else {
				throw new IllegalStateException(
						"Param has no value - call isValueSet() to check");
			}
		} else {
			throw new IllegalStateException("Configuration Paramater stores a "
					+ this.type.getTypeAsString() + " type parameter, not a "
					+ ParamTypes.FLOAT_S + ".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Param#getValueDouble()
	 */
	public double getValueDouble() throws RemoteException {
		if (this.type.getTypeAsInt() == ParamTypes.DOUBLE) {
			if (null != this.parameterValueDouble) {
				return this.parameterValueDouble.doubleValue();
			} else {
				throw new IllegalStateException(
						"Param has no value - call isValueSet() to check");
			}
		} else {
			throw new IllegalStateException("Configuration Paramater stores a "
					+ this.type.getTypeAsString() + " type parameter, not a "
					+ ParamTypes.DOUBLE_S + ".");
		}
	}

	private int generateBaseHashCode() {
		int result = OFFSET + this.name.hashCode();
		if (this.value != null) {
			result = result * MULTIPLIER + this.value.hashCode();
		}
		return result;
	}

	public int hashCode() {
		return generateBaseHashCode();
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (other == null)
			return false;

		if (this.getClass() != other.getClass())
			return false;

		P4ParamImpl otherConfigParam = (P4ParamImpl) other;

		try {

			if ((this.getName() == null) ^ (otherConfigParam.getName() == null)) {
				return false;
			}
			if (!(this.getName().equals(otherConfigParam.getName()))) {
				return false;
			}

			if ((this.getDisplayName() == null)
					^ (otherConfigParam.getDisplayName() == null)) {
				return false;
			}
			if (!(this.getDisplayName().equals(otherConfigParam
					.getDisplayName()))) {
				return false;
			}

			if ((this.getType() == null) ^ (otherConfigParam.getType() == null)) {
				return false;
			}
			if (!(this.getType().getTypeAsString().equals(otherConfigParam
					.getType().getTypeAsString()))) {
				return false;
			}

			if (this.valueShallBeHidden() != otherConfigParam
					.valueShallBeHidden()) {
				return false;
			}

			if (this.isValueSet() != otherConfigParam.isValueSet()) {
				return false;
			}

			try {
				switch (this.type.getTypeAsInt()) {
				case ParamTypes.BOOLEAN:
					if (this.getValueBoolean() != otherConfigParam
							.getValueBoolean()) {
						return false;
					}
					break;
				case ParamTypes.BYTE:
					if (this.getValueByte() != otherConfigParam.getValueByte()) {
						return false;
					}
					break;
				case ParamTypes.DOUBLE:
					if (this.getValueDouble() != otherConfigParam
							.getValueDouble()) {
						return false;
					}
					break;
				case ParamTypes.FLOAT:
					if (this.getValueFloat() != otherConfigParam
							.getValueFloat()) {
						return false;
					}
					break;
				case ParamTypes.INT:
					if (this.getValueInt() != otherConfigParam.getValueInt()) {
						return false;
					}
					break;
				case ParamTypes.LONG:
					if (this.getValueLong() != otherConfigParam.getValueLong()) {
						return false;
					}
					break;
				case ParamTypes.SHORT:
					if (this.getValueShort() != otherConfigParam
							.getValueShort()) {
						return false;
					}
					break;
				case ParamTypes.STRING:
					if (!(this.getValueString().equals(otherConfigParam
							.getValueString()))) {
						return false;
					}
					break;
				default:
					return false;
				}
			} catch (IllegalStateException isE) {
				return false;
			}

		} catch (RemoteException re) {
			return false;
		}

		return true;
	}

}
