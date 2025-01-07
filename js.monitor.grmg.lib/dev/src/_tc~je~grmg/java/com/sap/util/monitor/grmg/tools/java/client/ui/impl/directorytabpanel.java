/*
 *  last change 2004-02-03
 */

package com.sap.util.monitor.grmg.tools.java.client.ui.impl;

import com.sap.util.monitor.grmg.tools.java.client.ui.*;
import com.sap.util.monitor.grmg.tools.java.client.*;
import com.sap.util.monitor.grmg.tools.runtime.*;
import com.sap.util.monitor.grmg.tools.java.client.grmg.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.GridBagConstraints;
import java.util.*;
import java.io.*;

import org.w3c.dom.*;
//import org.xml.sax.*;

public class DirectoryTabPanel extends ITabPanel {

  public JComponent jcomp;
  public ImageIcon icon;
  public String tipText;
  File selectedFile; 
  String resourceName = "";
  String currentDir;
  //int runCount=0;
  Vector fileVec = new Vector();
  //ProgressMonitor monitor;
  JProgressBar jpb;
  int progressCounter=0;
  //Properties uiProps = new Properties();

  public DirectoryTabPanel(){
  }

  public JComponent getComponent(){
   return jcomp;
  }
  
  public ImageIcon getImageIcon(){
   return icon;
  }
  
  public String getTipText(){
   return tipText;
  }

  public void setCurrentDirectory(String currDir){
  
   currentDir = currDir;   
  }
  
	public String getCurrentDirectory(){
  
	 return currentDir;
	}

  public void setSelectedFile(File newFile){  	
  	selectedFile = newFile;
  }
    
	public File getSelectedFile(){  	
		return selectedFile;
	}

  public void buildComponent(){

   GridBagConstraints gridConstr = new GridBagConstraints();
   gridConstr.fill = GridBagConstraints.BOTH;

   jcomp = new JPanel();
   jcomp.setName("GRMG Upload");
   jcomp.setLayout(new BorderLayout());
	 
	 final JPanel pListPanel = new JPanel();

   // North panel
   // -----------
   JPanel p1 = new JPanel();
   p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

   final JFileChooser p1Chooser = new JFileChooser();
   p1Chooser.setCurrentDirectory(new File(getDataManager().getWorkingDirectory()));
   
	 javax.swing.filechooser.FileFilter defaultFilter = new javax.swing.filechooser.FileFilter(){
			 public boolean accept(File file){
				 if(file.toString().endsWith(".xml") || file.isDirectory())
					return true;
				 else
					return false;
					}	 
			 public String getDescription(){	  
				return "XML Files";	
			 }};
			 
	 p1Chooser.addChoosableFileFilter(defaultFilter);
	  
		p1Chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter(){
		 public boolean accept(File file){
				return true;
				}	 
		 public String getDescription(){	  
			return "All Files";	
		 }});

	 p1Chooser.setAcceptAllFileFilterUsed(false);
	 p1Chooser.setFileFilter(defaultFilter);
	 
	 final JTextField resourceText = new JTextField();
	 final JList scenarioList = new JList();
	 //monitor = getDataManager().getMonitor();
	 jpb = getDataManager().getProgressBar();
	 final JTextPane ePane = new JTextPane();
	 ePane.setFont(new Font("SansSerife", Font.PLAIN, 12));		
	 ePane.setSize(3000, 100);
	 //final JScrollPane pscroll = new JScrollPane(ePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	 final JScrollPane pscroll = new JScrollPane(ePane);
	 pscroll.setSize(3000, 200);

   JButton bLoad = getDataManager().getBLoad(); 	 
	 bLoad.addActionListener(
	  new ActionListener(){
		 public void actionPerformed(ActionEvent e) {
			
			progressCounter = 50;
			//monitor.setProgress(progressCounter);
			jpb.setValue(progressCounter);
			jpb.setString("Waiting for Scenario Test Run ...");		

		 	String fillName="";
		 	getDataManager().setResourceName(resourceName = resourceText.getText());
			getDataManager().setGrmgXmlFile(selectedFile = p1Chooser.getSelectedFile());
			
			progressCounter = 200;
			//monitor.setProgress(progressCounter);		
			jpb.setValue(progressCounter);		
			//jpb.setString("loading ...");		
							 
			//monitor.setNote("waiting for response...");		 			   		   			
			
			ePane.selectAll();
			ePane.replaceSelection("");
			ePane.updateUI();
			pscroll.updateUI();

			 //System.out.println("Selected resource: " + resourceName);
			if(selectedFile != null){
			  //System.out.println("Selected file: " + selectedFile.toString());
			 //getDataManager().setChooserDirectory(selectedFile.getParent());

			 p1Chooser.setCurrentDirectory(selectedFile.getParentFile());
			}
			 progressCounter = 400;
			 //monitor.setProgress(progressCounter);	
			 jpb.setValue(progressCounter);
			 //jpb.setString("...");					 		

			 GrmgDocumentAnalyzer.setDocument(getDataManager().getGrmgXmlStream());
			 HashMap nmap = GrmgDocumentAnalyzer.getScenarioMap();

			 Vector listVect = new Vector();
       Iterator iterScenNames = nmap.keySet().iterator();

       if(!GrmgDocumentAnalyzer.getErrorMessage().equals("")){
        listVect.add(GrmgDocumentAnalyzer.getErrorMessage());
				GrmgDocumentAnalyzer.setErrorMessage("");
       }
       else{ 
		    while(iterScenNames.hasNext()){
			   Object scenname = iterScenNames.next();
			   int diff = 60 - scenname.toString().length();			   

			    for(int i = 0 ; i < diff ; i += 1)
					 fillName = fillName + " ";  
			     
	       listVect.add(scenname + fillName);
			  }
       }
		   scenarioList.setListData(listVect);			 			 
		  //}
			//else{
			 // System.out.println("No files selected!");
			//}
			progressCounter = 600;
			//monitor.setProgress(progressCounter);		
			jpb.setValue(progressCounter);	
			//jpb.setString("...");						
							 
			//monitor.setNote("loading...");		 			   		   			
		}});

	 JButton bReset = getDataManager().getBReset();
	 bReset.addActionListener(
	  new ActionListener(){
		 public void actionPerformed(ActionEvent e) {
			getDataManager().setResourceName(resourceName = "");
			resourceText.setText("");
			//getDataManager().refreshProgressBar(true);
			jpb.setString("Waiting for Upload ...");
			jpb.setValue(0);
			
			if(selectedFile != null){
			 // System.out.println("De-selected file: " + selectedFile.toString());  			
			 getDataManager().setGrmgXmlFile(selectedFile = null);
			}			
			p1Chooser.setSelectedFile(null);
			p1Chooser.cancelSelection();
			GrmgDocumentAnalyzer.setDocument(null);
						
			Vector listVector = new Vector();			
			scenarioList.setListData(listVector);						
			p1Chooser.updateUI();

			ePane.selectAll();
			ePane.replaceSelection("");
			ePane.updateUI();
			pscroll.updateUI();
		}});


	 JButton runTest = getDataManager().getBRunTest();
   runTest.addActionListener(new ActionListener(){
   	public void actionPerformed(ActionEvent e){
   	 try{
   	  String selectedScenario = ((String)scenarioList.getSelectedValue()).trim();
   		getDataManager().setSelectedScenarioName(selectedScenario);
   		   		
   		//System.out.println("Scenario: " + selectedScenario);
   		String scenurl = GrmgDocumentAnalyzer.getScenarioUrlFromName(selectedScenario);
			Document scenDoc = GrmgDocumentAnalyzer.getScenarioAsDocument(selectedScenario);
			      
			URLConnector uconn = new URLConnector();
			//ProgressMonitor myMonitor = uconn.getProgressMonitor();

			ByteArrayOutputStream bout = uconn.getResponseData(scenDoc, scenurl);

			getDataManager().fillByteArrayOutput(bout);
			
			bout.close();
			
			progressCounter = 700;
			//monitor.setProgress(progressCounter);		
			jpb.setValue(progressCounter);		
			jpb.setString("Getting response ...");		

							 
			//monitor.setNote("running scenario...");

			ByteArrayOutputStream barrayout = getDataManager().getByteArrayOutput();
					  
		  String root = getDataManager().getWorkingDirectory();

			progressCounter = 800;
			//monitor.setProgress(progressCounter);	
			jpb.setValue(progressCounter);													 
		 
		  //runCount += 1;		  
		  //String runCounter = new Integer(runCount).toString();		  
		  String timestamp = TimePunch.getDate() + TimePunch.getTime();	  
		  File outputfile = new File(root + File.separator + "grmg" + timestamp + ".log");		  
		  
		  barrayout.write(("\nOutput logged in file " + outputfile.toString()).getBytes());
		  		  
			//File outputfile = new File(root + "grmgOutput.txt");		  		  		  	 
			FileOutputStream fout = new FileOutputStream(outputfile);
			barrayout.writeTo(fout);
			fout.close();
			barrayout.close();

			progressCounter = 850;
			//monitor.setProgress(progressCounter);			
			jpb.setValue(progressCounter);		
						 
			//monitor.setNote("writing to console...");		
			
			//FileInputStream fin = new FileInputStream(outputfile);
		  ePane.setPage(outputfile.toURL());
		  //ePane.read(fin, "URL Response");
		  //fin.close();
		  
		  ePane.repaint();
		 	ePane.updateUI();
		 	pscroll.updateUI();
		 	
			for(int k = 851; k < 1001; k += 1){				
			 progressCounter = k;
			 //monitor.setProgress(progressCounter);	
			 jpb.setString("End of GRMG Scenario Test");				 
			 jpb.setValue(progressCounter);	
			}

			//monitor.close();

      fileVec.add(outputfile);
	 	 }
		 catch(IOException ex){
		  ex.printStackTrace();
		 }								
		 catch(NullPointerException ex){
			 //ex.printStackTrace();
       debug(ex.getMessage());
		 }									
   	}   	
   });
	 
	 p1Chooser.setEnabled(false);
   p1Chooser.setBorder(new EmptyBorder(10, 10, 10, 10)); 
   p1Chooser.setControlButtonsAreShown(false);
	 	 
	 JPanel pFrameChooser = new JPanel();
	 pFrameChooser.setLayout(new BoxLayout(pFrameChooser, BoxLayout.X_AXIS));
	 pFrameChooser.setBorder(new TitledBorder(new EtchedBorder(), "From File System:"));
	 pFrameChooser.addMouseListener(new MouseListener(){
		public void mouseClicked(MouseEvent me){
			if(progressCounter == 1000){
				progressCounter = 0;				
				//getDataManager().refreshMonitor();
				//getDataManager().refreshProgressBar(true);
				//monitor = getDataManager().getMonitor();
				jpb.setValue(0);
				jpb.setString("Waiting for Upload ....");		
			}
		}
		public void mouseEntered(MouseEvent me){
			if(progressCounter == 1000){
				progressCounter = 0;				
				//getDataManager().refreshMonitor();
				//getDataManager().refreshProgressBar(true);
				//monitor = getDataManager().getMonitor();
				jpb.setValue(0);
				jpb.setString("Waiting for Upload ....");		
			}
		}
		public void mouseReleased(MouseEvent me){
		}
		public void mouseExited(MouseEvent me){
		}
		public void mousePressed(MouseEvent me){
			if(progressCounter == 1000){
				progressCounter = 0;				
				//getDataManager().refreshMonitor();
				//getDataManager().refreshProgressBar(true);
				//monitor = getDataManager().getMonitor();
				jpb.setValue(0);
				jpb.setString("Waiting for Upload ....");		
			}
		}
	 });

	 pFrameChooser.add(p1Chooser);
	 
	 p1.add(pFrameChooser); 

	 //JPanel pListPanel = new JPanel();
	 pListPanel.setLayout(new BoxLayout(pListPanel, BoxLayout.X_AXIS));
	 pListPanel.setBorder(new TitledBorder(new EtchedBorder(), "Select Scenario:"));

	 JScrollPane ps = new JScrollPane(scenarioList);
	 ps.setMaximumSize(new Dimension(500,100));

	 // add scroll pane in center of south panel
	 pListPanel.add(ps);
	 pListPanel.add(Box.createRigidArea(new Dimension(10,10)));

	 pListPanel.add(runTest);
	 pListPanel.add(Box.createRigidArea(new Dimension(10,10)));
	 p1.add(pListPanel);

   // Center panel
   // ------------
   JPanel p2 = new JPanel();
   p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
   p2.setBorder(new TitledBorder(new EtchedBorder(), ""));

    // a. scroll pane (containing a list) in Center panel
    //

		JPanel pResource = new JPanel();
		pResource.setLayout(new BoxLayout(pResource, BoxLayout.X_AXIS));
		pResource.setBorder(new TitledBorder(new EtchedBorder(), "From Java Resource:"));

		pResource.add(resourceText);
		pResource.addMouseListener(new MouseListener(){
		public void mouseClicked(MouseEvent me){
			if(progressCounter == 1000){
				progressCounter = 0;				
				//getDataManager().refreshMonitor();
				//getDataManager().refreshProgressBar(true);
				//monitor = getDataManager().getMonitor();
				jpb.setValue(0);
				jpb.setString("Waiting for Upload ....");		
			}
		}
		public void mouseEntered(MouseEvent me){
			if(progressCounter == 1000){
				progressCounter = 0;				
				//getDataManager().refreshMonitor();
				//getDataManager().refreshProgressBar(true);
				//monitor = getDataManager().getMonitor();
				jpb.setValue(0);
				jpb.setString("Waiting for Upload ....");		
			}
		}
		public void mouseReleased(MouseEvent me){
		}
		public void mouseExited(MouseEvent me){
		}
		public void mousePressed(MouseEvent me){
			if(progressCounter == 1000){
				progressCounter = 0;				
				//getDataManager().refreshMonitor();
				//getDataManager().refreshProgressBar(true);
				//monitor = getDataManager().getMonitor();
				jpb.setValue(0);
				jpb.setString("Waiting for Upload ....");		
			}
		}
	 });
		p2.add(pResource);

		p2.add(Box.createRigidArea(new Dimension(30,10)));

		JPanel pSc = new JPanel();
		pSc.setLayout(new BoxLayout(pSc, BoxLayout.X_AXIS));
		pSc.setBorder(new TitledBorder(new EtchedBorder(), "Load selected GRMG Data:"));
		pSc.add(Box.createRigidArea(new Dimension(10,10)));
		pSc.add(bLoad);
		pSc.add(Box.createRigidArea(new Dimension(10,10)));
		pSc.add(bReset);
		pSc.add(Box.createRigidArea(new Dimension(10,10)));

    p2.add(pSc);

	// East panel
	// ------------
    
  // South panel
  // -----------

    JPanel pS = new JPanel();
    //neu
		//pS.setLayout(new BoxLayout(pS, BoxLayout.X_AXIS));
		//pS.setBorder(new TitledBorder(new EtchedBorder(), ""));
		pS.setLayout(new BorderLayout());

		JPanel pOut = new JPanel();
		pOut.setLayout(new BoxLayout(pOut, BoxLayout.X_AXIS));
		pOut.setBorder(new TitledBorder(new EtchedBorder(), "Console:"));

		//pscroll = new JScrollPane(ePane, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pscroll.updateUI();
		pOut.setMaximumSize(new Dimension(3000,200));
		pOut.add(pscroll);
		pOut.add(Box.createRigidArea(new Dimension(10,150)));

		///*
		// JProgressBar jpb = new JProgressBar();
		
		//jpb.setMinimum(0);
		//jpb.setMaximum(1000);
		pS.add(jpb, BorderLayout.SOUTH);
		//*/

		pS.add(pOut, BorderLayout.NORTH);
		

   //////////////////////////////////
   // add NORTH panel to jcomp
   jcomp.add(p1, BorderLayout.NORTH);
   //
	 // add CENTER panel to jcomp
	 jcomp.add(p2, BorderLayout.CENTER);
	 //
	 // add CENTER panel to jcomp
	 //jcomp.add(pScen, BorderLayout.EAST);
	 //
   // add South panel to jcomp
   jcomp.add(pS, BorderLayout.SOUTH);
 }

  public void buildImageIcon(){
   icon = new ImageIcon((ConnectorConsole.class).getResource(ConnectorConsole.RESOURCE_LOCATION + "/sap_logo.gif"));
  }

  public void buildTipText(){
   tipText = "Edit and List";
  }

  public void actionPerformed(ActionEvent e){}

  void addComponent(JComponent jcomp, JComponent componentToAdd, GridBagConstraints gbc,
                    int gridx, int gridy, int gridheight, int gridwidth){

    gbc.gridx = gridx;
    gbc.gridy = gridy;
    gbc.gridheight = gridheight;
    gbc.gridwidth = gridwidth;
    gbc.fill = GridBagConstraints.BOTH;
    jcomp.add(componentToAdd, gbc);
  }
  
	/**
	 * @return
	 */
	public Vector getFiles() {
		return fileVec;
	}

  public void cleanUp(){
  	
  	for(int i = 0; i < fileVec.size(); i += 1){
  		
			if(((File)fileVec.get(i)).delete()){
			 //System.out.println("Temporary file " + ((File)fileVec.get(i)).toString() + " deleted");
			}
			else{
				((File)fileVec.get(i)).deleteOnExit();
			}
  	} 	
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
