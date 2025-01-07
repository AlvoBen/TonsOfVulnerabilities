package com.sap.sdo.testcase.internal.pojo.ex;

import java.util.List;

import com.sap.sdo.api.SdoTypeMetaData;
@SdoTypeMetaData(uri="http://projection")
public interface School {
	String getName();
	void setName(String name);
	List<Student> getStudents();
	void setStudents(List<Student> s);
	List<Course> getCourses();
	void setCourses(List<Course> c);
}
