package com.sap.sdo.testcase.internal.pojo.ex;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;
@SdoTypeMetaData(uri="http://projection")
public interface Course {
	String getName();
	void setName(String name);
	
	@SdoPropertyMetaData(opposite="courses")
	School getSchool();
	void setSchool(School s);
	
	@SdoPropertyMetaData(opposite="courses")
	List<Student> getStudents();
	void setStudents(List<Student> s);
}
