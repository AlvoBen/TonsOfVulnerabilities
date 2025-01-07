/*
 *  last change 2004-02-03
 */

package com.sap.util.monitor.grmg.tools.java.client.ui;

import com.sap.util.monitor.grmg.tools.java.client.ui.impl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ConnectorPaneList extends JPanel implements ActionListener {

 int tabComponents=0;
 int componentCounter = 0;
 private JComponent[] jcomps;
 private ImageIcon[] images;
 private String[] tips;
 private Vector jadeTabPanelList = new Vector();
 private Vector components = new Vector();
 private Vector icons = new Vector();
 private Vector tipTexts = new Vector();
 private Vector paneList = new Vector();
 private PanelDataManager dManager;

////////////////
// CONSTRUCTORS
///////////////

 ConnectorPaneList(){

  // all components will be added upon creation of a JadeTabbedPaneList instance
  // 1. a JTabbedPane
  // 2. Therein add the list of tab panels

  dManager = new PanelDataManager();
  //dManager.setMonitor(this, "GRMG Progress Monitor", "", 0, 1000);
	//dManager.setMonitor(this, "Select scenario from list ...", "", 0, 1000);
	dManager.setProgressBar(0, 1000);
  // ImageIcon icon = new ImageIcon((JadeFrame.class).getResource(JadeFrame.RESOURCE_LOCATION + "/my_logo.gif"));
  
  JTabbedPane tabbedPane = new JTabbedPane();
  tabbedPane.getName();
  

  // add list of JadeTabPanel instances:
  // 1.
  // componentCounter += addTabPanel(new SystemTabPanel(), jadeTabPanelList);
  // 3.
  // componentCounter += addTabPanel(new DeployTabPanel(), jadeTabPanelList);
  // 4.
  // componentCounter += addTabPanel(new ConfigTabPanel(), jadeTabPanelList);
  // 5.
  //componentCounter += addTabPanel(new DirectoryTabPanel(), jadeTabPanelList);
	// 2.
	//componentCounter += addTabPanel(new ApplicationTabPanel(), jadeTabPanelList);
  // etc.

  //createTabsFromList(tabbedPane, this);

  //tabbedPane.setSelectedIndex(0);
  //add(tabbedPane);
    
  // other components
  //
  
  JPanel inlet = new JPanel();
  DirectoryTabPanel dirpan = new DirectoryTabPanel();
  addPanel(inlet, dirpan);
 }

///////////
// METHODS
//////////

  // addTabPanel
  //
  int addTabPanel(ITabPanel jtpan, Vector tabList){
   jtpan.register(dManager);
	 jtpan.buildComponent();
	 jtpan.buildTipText();
	 jtpan.buildImageIcon();

   tabList.add(jtpan);
   return 1;
  }

	// addTabPanel
	//
	int addPanel(JPanel inlet, ITabPanel jtpan){

	 jtpan.register(dManager);
	 jtpan.buildComponent();
	 jtpan.buildTipText();
	 jtpan.buildImageIcon();

	 inlet.add(jtpan.getComponent());
	 add(inlet);
	 paneList.add(jtpan);
	 return 1;
	}

  // getJComponents
  //
  Vector getJComponents(Vector jTabPanelList){
   for(int j=0; j < jTabPanelList.size(); j ++)
    components.add(((ITabPanel)jTabPanelList.elementAt(j)).getComponent());
   return components;
  }

  // getImageIcons
  //
  Vector getImageIcons(Vector jTabPanelList){
   for(int j=0; j < jTabPanelList.size(); j ++)
    icons.add(((ITabPanel)jTabPanelList.elementAt(j)).getImageIcon());
   return icons;
  }

  // getTipTexts
  //
  Vector getTipTexts(Vector jTabPanelList){
   for(int j=0; j < jTabPanelList.size(); j ++)
    tipTexts.add(((ITabPanel)jTabPanelList.elementAt(j)).getTipText());
   return tipTexts;
  }

  public void actionPerformed(ActionEvent e){}

  // method createTabsFromList
  //
  protected void createTabsFromList(JTabbedPane jtp, ConnectorPaneList jtplist) {

   Vector panels = jtplist.getJComponents(this.jadeTabPanelList);
   Vector imageIcons = jtplist.getImageIcons(this.jadeTabPanelList);
   Vector tipTexts = jtplist.getTipTexts(this.jadeTabPanelList);

    for(int j=0; j < jtplist.componentCounter; j ++)
     {
      jtp.add((Component)panels.get(j), j);
      jtp.setIconAt(j,(ImageIcon)imageIcons.get(j));
      jtp.setToolTipTextAt(j,(String)tipTexts.get(j));
     }
  }
  
  public void cleanUp(){
  	
  	for(int j = 0; j < paneList.size(); j += 1){  		
  		((ITabPanel)paneList.get(j)).cleanUp();
  	}  	
  }
}
