/*
 *  last change 2004-02-03
 */

package com.sap.util.monitor.grmg.tools.java.client.ui;

import java.io.*;
//import auxiliary.tools.java.client.ui.*;
import javax.swing.*;
//import java.util.*;
//import javax.swing.border.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.GridBagConstraints;

public class PanelDataManager {

 File grmgXmlFile;
 File scenarioXmlFile;
 String resourceName;
 String scenarioName;
 boolean isFile = true;
 JButton bLoad, bReset, bRunTest;
 String selectedScenarioName;
 //BufferedOutputStream bufferOutput;
 ByteArrayOutputStream barrayOutput;
 String workingDirectory;
 //File chooserDirectory;
 //Properties systemProps;
 ProgressMonitor monitor;
 JProgressBar jProgBar;
 int barMin;
 int barMax;
 JComponent monitorComp;
 String monitorTitle="";
 String monitorNote="";
 int monitorMin;
 int monitorMax;

 
 public PanelDataManager(){
 	
 	bLoad = new JButton();
 	bLoad.setText("Load");
 	bReset = new JButton();
 	bReset.setText("Reset");
 	bRunTest = new JButton();
	bRunTest.setText("Run Scenario"); 	
	barrayOutput = new ByteArrayOutputStream();
	workingDirectory = System.getProperty("user.dir");
	//systemProps = new Properties();
 }
 
 /*
 public void fillOutputBuffer(ByteArrayOutputStream barrayStream){
 	
 	try{
 	 barrayStream.writeTo(bufferOutput);
 	}
 	catch(IOException e){
 	 e.printStackTrace();
 	}
 }
 */
 
 public void fillByteArrayOutput(ByteArrayOutputStream barrayStream){
 	
	try{
		//if(barrayOutput != null){
		 barrayOutput = new ByteArrayOutputStream();	
	   barrayStream.writeTo(barrayOutput);
		//}
	}
	catch(IOException e){
	 e.printStackTrace();
	}
 }

 /**
 * @return
 */
 public File getGrmgXmlFile() {
	return grmgXmlFile;
 }

 /**
 * @return
 */
 public String getResourceName() {
	return resourceName;
 }

 /**
 * @return
 */
 public String getScenarioName() {
	return scenarioName;
 }

 /**
 * @param file
 */
 public void setGrmgXmlFile(File file) {
	grmgXmlFile = file;
 }

 /**
 * @param string
 */
 public void setResourceName(String string) {
	resourceName = string;
 }

 /**
 * @param string
 */
 public void setScenarioName(String string) {
	scenarioName = string;
 }

 /**
 * @return
 */
 public File getScenarioXmlFile() {
	return scenarioXmlFile;
 }

 /**
 * @param file
 */
 public void setScenarioXmlFile(File file) {
	scenarioXmlFile = file;
 }

 /**
 * @return
 */
 public boolean isFile() {
	return isFile;
 }

 public InputStream getGrmgXmlStream(){
 	
  if(grmgXmlFile != null && grmgXmlFile.isFile()){
	 isFile = true;
	 try{
	  return new FileInputStream(grmgXmlFile);
	 }
	 catch(FileNotFoundException e){
     debug(e.getMessage());
	 }
  }
  if(!resourceName.equals("")){
   //System.out.println("Resource: " + resourceName);	
	 isFile = false;
	 return PanelDataManager.class.getResourceAsStream(resourceName);
  } 
 	
 	isFile = false;
  System.out.println("Neither file nor resource found."); 
  return null;
 }

 /**
 * @return
 */
 public JButton getBLoad() {
	return bLoad;
 }

 /**
 * @return
 */
 public JButton getBReset() {
	return bReset;
 }

 /**
 * @return
 */
 public JButton getBRunTest() {
	return bRunTest;
 }

 /**
 * @return
 */
 public String getSelectedScenarioName() {
	return selectedScenarioName;
 }

 /**
 * @param string
 */
 public void setSelectedScenarioName(String string) {
	selectedScenarioName = string;
 }

 /**
 * @return
 */
 /*
 public BufferedOutputStream getBufferOutput() {
	return bufferOutput;
 }
 */
 
 /**
 * @return
 */
 public ByteArrayOutputStream getByteArrayOutput() {
	return barrayOutput;
 }

 /**
 * @return
 */
 public String getWorkingDirectory() {
	return workingDirectory;
 }

 /*
 public File getChooserDirectory() {
  InputStream propStream = PanelDataManager.class.getResourceAsStream("/javaClientUI.properties");
   try{
   	systemProps.load(propStream);
   }
   catch(IOException e){}
   
   return new File(systemProps.getProperty("LastVisitedDirectory"));   
 }

 public void setChooserDirectory(File file) {
 	
	InputStream propStream = PanelDataManager.class.getResourceAsStream("/javaClientUI.properties");
	 try{
		systemProps.load(propStream);
	 }
	 catch(IOException e){}
	 
	 systemProps.setProperty("LastVisitedDirectory", file.getAbsolutePath());
 }

 public File getInitialDirectory() {
	InputStream propStream = PanelDataManager.class.getResourceAsStream("/javaClientUI.properties");
	 try{
		systemProps.load(propStream);
	 }
	 catch(IOException e){}
   
	 return new File(systemProps.getProperty("InitialDirectory"));   
 }

 public void setInitialDirectory(File file) {
 	
	InputStream propStream = PanelDataManager.class.getResourceAsStream("/javaClientUI.properties");
	 try{
		systemProps.load(propStream);
	 }
	 catch(IOException e){}
	 
	 systemProps.setProperty("InitialDirectory", file.getAbsolutePath());
 }
 */

 /**
 * @return
 */
 public ProgressMonitor getMonitor() {
	return monitor;
 }

 /**
 * @param monitor
 */
 public void setMonitor(JComponent jcomp, String title, String note, int min, int max) {
 	
 	monitorComp = jcomp;
 	monitorTitle = title;
 	monitorNote = note;
 	monitorMin = min;
 	monitorMax = max;
 	 
	this.monitor = new ProgressMonitor(jcomp, title, note, min, max);
 }
 
 public void refreshMonitor(){
 	
 	if(monitorComp != null && monitorMin <= monitorMax)
   this.monitor = new ProgressMonitor(monitorComp, monitorTitle, monitorNote, 
                                      monitorMin, monitorMax);
 }
 
 /**
 * @return
 */
 public JProgressBar getProgressBar() {
	return jProgBar;
 }

 /**
 * @param monitor
 */
 public void setProgressBar(int min, int max) {
 	
	barMin = min;
	barMax = max;
 	 
	this.jProgBar = new JProgressBar(min, max);
	this.jProgBar.setString("Waiting for Upload ...");
	this.jProgBar.setStringPainted(true);
 }
 
 public void refreshProgressBar(boolean force){
 	
	if(barMin <= barMax || force)
	 setProgressBar(barMin, barMax);
	 this.jProgBar.repaint();	 
 }
 
 private void debug(String s) {
   //add logging here
 }

 private void log(String s) {
   //add logging here
 }

 private void log(String s, Exception e) {
   //add logging here
 }
}
