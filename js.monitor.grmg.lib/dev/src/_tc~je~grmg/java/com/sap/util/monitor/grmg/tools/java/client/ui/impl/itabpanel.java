/*
 *  last change 2004-01-08
 */

package com.sap.util.monitor.grmg.tools.java.client.ui.impl;

import javax.swing.*;

import com.sap.util.monitor.grmg.tools.java.client.ui.*;
//import auxiliary.tools.java.client.ui.*;

public abstract class ITabPanel //implements ActionListener{
{
	public PanelDataManager dManager;

  abstract public JComponent getComponent();
  abstract public ImageIcon getImageIcon();
  abstract public String getTipText();

  abstract public void buildComponent();
  abstract public void buildImageIcon();
  abstract public void buildTipText();
  
  public void cleanUp(){}

  public void register(PanelDataManager dMgr){
  	dManager = dMgr;
  }
  
  public PanelDataManager getDataManager(){
  	return dManager;
  }
  
  //abstract public void actionPerformed(ActionEvent e);
}
