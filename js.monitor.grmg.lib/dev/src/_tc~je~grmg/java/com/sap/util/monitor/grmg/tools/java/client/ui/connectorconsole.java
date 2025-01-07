/*
 *  last change 2004-02-03
 */

package com.sap.util.monitor.grmg.tools.java.client.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ConnectorConsole extends JFrame{

	ImageIcon logoImage = null;
	public static final String RESOURCE_LOCATION = "/com/sap/util/monitor/grmg/tools/java/client/ui/resources";

	public static void main (String[] args ){

	 // instantiate components
	 //
	 final ConnectorConsole jf = new ConnectorConsole();
	 ConnectorPaneList jtp = new ConnectorPaneList();
	 jf.setResizable(false);
	 Container content = jf.getContentPane();
	 GridBagLayout gbl = new GridBagLayout();
	 GridBagConstraints gbc = new GridBagConstraints();
	 content.setLayout(gbl);

	 //final ConnectorPaneList jtp = new ConnectorPaneList();

	 JPanel toolBarPanel = new JPanel();
	 toolBarPanel.setLayout(new BorderLayout());

	 JLabel logoLabel = new JLabel();
	 logoLabel.setIcon(jf.logoImage);

	 JMenuBar menuBar = new JMenuBar();

	 JMenu file = new JMenu("Connector");
   /*
	 JMenu edit = new JMenu("Edit");
	 JMenu search = new JMenu("Search");
	 JMenu view = new JMenu("View");
	 JMenu project = new JMenu("Project");
	 JMenu deploy = new JMenu("Deploy");
   */
   
   JMenuItem close = new JMenuItem("Exit");
   close.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		 //jtp.cleanUp();	 	
   	 System.exit(0);
	 }}); 
  
	 file.add(close);	 

   /* 
	 JMenuItem refresh = new JMenuItem("Refresh");
	 refresh.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		 jf.repaint();
	 }}); 
   
	 file.add(refresh);
	 */
	 	 
	 /*
	 file.add(new JMenuItem("Save ..."));
	 file.add(new JMenuItem("Exit ..."));
	 file.add(new JMenuItem("Run ..."));
   */
	 menuBar.add(file);
	 /*
	 menuBar.add(edit);
	 menuBar.add(search);
	 menuBar.add(view);
	 menuBar.add(project);
	 menuBar.add(deploy);
	 */

   // Add components
	 toolBarPanel.add(logoLabel, BorderLayout.EAST);
	 toolBarPanel.add(menuBar, BorderLayout.WEST);

	 // add components to frame
	 gbc.gridx = 0;
	 gbc.gridy = 0;
	 gbc.gridheight = 1;
	 gbc.gridwidth = 1;
	 gbc.fill = GridBagConstraints.BOTH;
	 content.add(toolBarPanel, gbc);
	 gbc.gridx = 0;
	 gbc.gridy = 1;
	 gbc.gridheight = 10;
	 gbc.gridwidth = 1;
	 gbc.fill = GridBagConstraints.BOTH;
	 content.add(jtp, gbc);

	 // final configuration of main frame
	 //
	 jf.setBackground(Color.lightGray);
	 jf.pack();
	 // here the size eventually will be set !
	 jf.setLocation(50,20);
	 jf.setVisible(true);	 
	}

	public ConnectorConsole(){

	 logoImage = new ImageIcon((ConnectorConsole.class).getResource(RESOURCE_LOCATION + "/sap_logo.gif"));
	 //String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try
		 {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel(lookAndFeel);
		 }
		catch(UnsupportedLookAndFeelException e){
		 e.printStackTrace();
		}
		catch(IllegalAccessException e){
		 e.printStackTrace();
		}
		catch(InstantiationException e){
		 e.printStackTrace();
		}
		catch(ClassNotFoundException e){
		 e.printStackTrace();
		}

	 addWindowListener(new WindowAdapter(){
		public void windowClosing (WindowEvent e){
		 //jtp.cleanUp();	 
		 //System.out.println("Monitor will be closed . . .");
		 System.exit(0);
		}
		public void windowIconified (WindowEvent e){
		 //System.out.println("Monitor minimized.");
		}
		public void windowDeiconified (WindowEvent e){
		 //System.out.println("Monitor maximized.");
		}
		public void windowActivated (WindowEvent e){
		 //System.out.println("Monitor activated.");		 
		}
		public void windowDeactivated (WindowEvent e){
		 //System.out.println("Monitor deactivated.");
		}
	 });

	 setBackground(Color.lightGray);
	 setTitle("GRMG Java Client");
	 setIconImage(logoImage.getImage());
	}
	
	public void cleanUp(){
   // to be filled	 	
	}
}
