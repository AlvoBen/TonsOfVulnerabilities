package com.sap.engine.services.dc.api.util;

/**
 * 
 * When the DC compiler compliance level is increased to java 1.5 replace this
 * interface with java.util.concurrent.Executor
 * 
 * @author I040924
 * 
 */
public interface Executor {

	void execute(Runnable r);

}