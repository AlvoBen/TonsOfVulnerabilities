/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.external.simpletypes.IStorageEfficiency;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 *
 */
public class StorageEfficiencyTest extends SdoTestCase {
	/**
     * @param pHelperContext
     */
    public StorageEfficiencyTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    Random generator = new Random(1234567890123456L);
	int l = 10000000;
	private IStorageEfficiency createAndFill(int index) {
		IStorageEfficiency ret = (IStorageEfficiency)_helperContext.getDataFactory().create(IStorageEfficiency.class);
		ret.setS1(String.valueOf(generator.nextInt(l)));
		ret.setS2(String.valueOf(generator.nextInt(l)));
		ret.setS3(String.valueOf(generator.nextInt(l)));
		ret.setS4(String.valueOf(generator.nextInt(l)));
		ret.setS5(String.valueOf(generator.nextInt(l)));
		ret.setI1(generator.nextInt(l));
		ret.setI2(generator.nextInt(l));
		ret.setI3(generator.nextInt(l));
		ret.setI4(generator.nextInt(l));
		ret.setI5(index);
		return ret;
	}
    public DataObject createLongList(int max) {
    	DataObject dynamicType = _helperContext.getDataFactory().create("commonj.sdo","Type");
    	dynamicType.set("name","container");
    	dynamicType.set("uri","lasttest");
    	dynamicType.set(TypeType.ABSTRACT, false);
    	dynamicType.set(TypeType.DATA_TYPE, false);
    	dynamicType.set(TypeType.SEQUENCED, false);
    	DataObject prop = dynamicType.createDataObject(TypeType.PROPERTY);
    	prop.set("name", "list");
    	prop.set("many", true);
    	prop.set("containment",true);
    	prop.set("type", _helperContext.getTypeHelper().getType(IStorageEfficiency.class));
    	Type t = _helperContext.getTypeHelper().define(dynamicType);
    	prop = _helperContext.getDataFactory().create("commonj.sdo","Property");
    	prop.set("name", "ce");
    	prop.set("type",t);
        prop.setBoolean("containment", true);
    	_helperContext.getTypeHelper().defineOpenContentProperty("lasttest", prop);
    	DataObject ret = _helperContext.getDataFactory().create(t);
        List<IStorageEfficiency> list = ret.getList("list");
    	for (int i=0; i<max; i++) {
    		list.add(createAndFill(i));
    	}
    	System.gc();
    	return ret;
	}
    public void xtestCreate() throws Exception {
    	int max = 5000;
    	DataObject ret = createLongList(max);
    	System.out.println("created "+max+" entries");
    	Thread.sleep(5000);
    }
    public void xtestSerializationFormat() throws Exception {
    		int max = 5;
    		DataObject ret = createLongList(max);
    		ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
    		ObjectOutputStream oos = new ObjectOutputStream(os);
    		long t1 = System.currentTimeMillis();
    		oos.writeObject(ret);
    		long time = t1-System.currentTimeMillis();
    		oos.flush();
    		oos.close();
    		os.close();
    		System.out.println(os.toString());
    		System.out.println("max="+max+",  size="+os.size()+", time="+time);
    		InputStream is = new ByteArrayInputStream(os.toByteArray());
    		t1 = System.currentTimeMillis();
    		ret = (DataObject)(new ObjectInputStream(is)).readObject();
    		time = t1-System.currentTimeMillis();
    		assertEquals(max,ret.getList("list").size());
    }
    @Test
    public void testSerializationSize() throws Exception {
    	for (int max=1; max<=10001; max+=1000) {
    		DataObject ret = createLongList(max);
    		ByteArrayOutputStream os = new ByteArrayOutputStream(10000);
    		ObjectOutputStream oos = new ObjectOutputStream(os);
    		long t1 = System.currentTimeMillis();
    		oos.writeObject(ret);
    		long time = t1-System.currentTimeMillis();
    		oos.flush();
    		oos.close();
    		os.close();
    		System.out.println("max="+max+",  size="+os.size()+", time="+time);
    		InputStream is = new ByteArrayInputStream(os.toByteArray());
    		t1 = System.currentTimeMillis();
    		ret = (DataObject)(new ObjectInputStream(is)).readObject();
    		time = t1-System.currentTimeMillis();
    		assertEquals(max,ret.getList("list").size());
    		System.out.println("restore time="+time);
    	}
    }
    @Test
    public void testXmlSerializationSize() throws Exception {
    	for (int max=1; max<=10001; max+=1000) {
    		DataObject ret = createLongList(max);
    		long t1 = System.currentTimeMillis();
    		String s = _helperContext.getXMLHelper().save(ret, "lasttest", "ce");
    		long time = t1-System.currentTimeMillis();
    		System.out.println("max="+max+",  size="+s.length()+", time="+time);
    		t1 = System.currentTimeMillis();
    		ret = _helperContext.getXMLHelper().load(s).getRootObject();
    		time = t1-System.currentTimeMillis();
    		System.out.println("restore time="+time);
    	}
    }

    @Test
    public void testIndexdAccess() throws Exception {
        int number=2500;
        DataObject ret = createLongList(number);
        for (int n=0; n<3; ++n) {
            long t1 = System.currentTimeMillis();
            for (int i=0; i<number; ++i) {
                ret.get("list[i5="+i+"]");
            }
            long time = System.currentTimeMillis()-t1;
            System.out.println("number="+number+", time="+time);
        }

        int value = 0;
        long t1 = System.currentTimeMillis();
        for (int n=0; n<number; ++n) {
            ret.get("list[i5="+value+"]");
        }
        long time = System.currentTimeMillis()-t1;
        System.out.println("first element number="+number+", time="+time);

        value = number/2;
        t1 = System.currentTimeMillis();
        for (int n=0; n<number; ++n) {
            ret.get("list[i5="+value+"]");
        }
        time = System.currentTimeMillis()-t1;
        System.out.println("middle element number="+number+", time="+time);

        value = number-1;
        t1 = System.currentTimeMillis();
        for (int n=0; n<number; ++n) {
            ret.get("list[i5="+value+"]");
        }
        time = System.currentTimeMillis()-t1;
        System.out.println("last element number="+number+", time="+time);
    }

    @Test
    public void testMethodAccess() {
        System.out.println("testMethodAccess");
        for (int max=1; max<=50001; max+=10000) {
            long t1 = System.currentTimeMillis();
            DataObject ret = createLongList(max);
            long t2 = System.currentTimeMillis();
            List<IStorageEfficiency> list = ret.getList("list");
            for (IStorageEfficiency efficiency : list) {
                int i;
                String s;
                i = efficiency.getI1();
                i = efficiency.getI2();
                i = efficiency.getI3();
                i = efficiency.getI4();
                i = efficiency.getI5();
                s = efficiency.getS1();
                s = efficiency.getS2();
                s = efficiency.getS3();
                s = efficiency.getS4();
                s = efficiency.getS5();
            }
            long t3 = System.currentTimeMillis();
            System.out.println("max="+max+", set="+(t2 - t1)+", get="+(t3 - t2)+", all="+(t3 - t1));
        }
    }

}
