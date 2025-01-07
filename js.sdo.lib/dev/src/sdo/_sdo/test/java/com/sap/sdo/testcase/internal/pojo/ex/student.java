package com.sap.sdo.testcase.internal.pojo.ex;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;
@SdoTypeMetaData(uri="http://projection")
public interface Student {
	String getName();
	void setName(String name);
	
	@SdoPropertyMetaData(opposite="students")
	School getSchool();
	void setSchool(School s);
	
	@SdoPropertyMetaData(opposite="students")
	List<Course> getCourses();
	void setCourses(List<Course> c);
}
