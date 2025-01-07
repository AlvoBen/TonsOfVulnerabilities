package com.sap.sdo.impl.objects.projections;

import java.util.HashMap;
import java.util.Map;

import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public abstract class AbstractProjectionMappingStrategy {

	public AbstractProjectionMappingStrategy() {
		super();
	}
	protected abstract Property getProperty(Type main, Property mine);
    public abstract Property getOpenProperty(HelperContext mainContext, Property mine);
    
	public int[] getPropertyMap(Type me, Type main) {
		Map<Type,int[]> map = (Map<Type,int[]>)((SdoType)me).getExtraData(getExtraGroup(), getExtraName());
		if (map == null) {
			map = new HashMap<Type,int[]>();
			((SdoType)me).putExtraData(getExtraGroup(), getExtraName(), map);
		}
		int[] ret = map.get(main);
		if (ret == null) {
			ret = calculateMapping(me,main);
			map.put(main,ret);
		}
		return ret;
	}
	public int[] getInversePropertyMap(Type me, Type main) {
		Map<Type,int[]> map = (Map<Type,int[]>)((SdoType)me).getExtraData(getExtraGroup(), getInverseName());
		if (map == null) {
			map = new HashMap<Type,int[]>();
			((SdoType)me).putExtraData(getExtraGroup(), getInverseName(), map);
		}
		int[] ret = map.get(main);
		if (ret == null) {
			ret = calculateInverseMapping(me,main);
			map.put(main,ret);
		}
		return ret;
	}
	private int[] calculateInverseMapping(Type me, Type main) {
		int[] ret = new int[main.getProperties().size()];
		for (int i=0; i<ret.length; i++) {
			Property his = (Property)main.getProperties().get(i);
			Property mine = getProperty(me, his);

			if (mine == null) {
				ret[i] = -1;
			} else {
				ret[i] = ((SdoProperty)mine).getIndex();
			}
			
		}
		return ret;
	}
	protected int[] calculateMapping(Type me, Type main) {
		int[] ret = new int[me.getProperties().size()];
		for (int i=0; i<ret.length; i++) {
			Property mine = (Property)me.getProperties().get(i);
			Property his = getProperty(main, mine);
			if (his == null) {
				throw new RuntimeException("Types are not compatible");
			}
			ret[i] = ((SdoProperty)his).getIndex();
		}
		return ret;
	}
	protected String getExtraGroup() {
		return "contextProjections";
	}
	protected String getExtraName() {
		return "TypeMap";
	}
	protected String getInverseName() {
		return "InverseTypeMap";
	}

}