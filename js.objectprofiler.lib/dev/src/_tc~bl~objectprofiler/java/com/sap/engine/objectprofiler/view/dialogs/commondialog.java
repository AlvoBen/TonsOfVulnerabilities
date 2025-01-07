package com.sap.engine.objectprofiler.view.dialogs;

import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: pavel-b
 * Date: 2005-12-27
 * Time: 10:20:34
 * To change this template use File | Settings | File Templates.
 */
public abstract class CommonDialog extends JDialog {
  public boolean canceled = false;

  protected GraphVizualizerPanel vizualizer = null;

  protected JButton close = new JButton("Close");
  protected JButton ok = new JButton("OK");
  protected Dimension bsize = new Dimension(90,25);

  public CommonDialog(GraphVizualizerPanel vizualizer, String title) {
    this(vizualizer, title, true);
  }



  public CommonDialog(GraphVizualizerPanel vizualizer, String title, boolean modal) {
    super((Frame)vizualizer.getTopLevelAncestor(), title, modal);

    this.vizualizer = vizualizer;
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exit();
      }
    });
  }

  public void showDialog() {
    init();
    createComponents();
    center();

    setResizable(false);
    setVisible(true);
  }


  protected void exit() {
    canceled = true;

    setVisible(false);
    dispose();
  }

  protected abstract void ok();

  protected void center() {
    if (vizualizer.isVisible()) {
      centerInParent();
    } else {
      centerInScreen();
    }
  }


  public void centerInScreen()
  {
    Dimension dim = this.getToolkit().getScreenSize();
    Rectangle bounds = this.getBounds();
    setLocation((dim.width - bounds.width) / 2,
            (dim.height - bounds.height) / 2);
    requestFocus();
  }

  public void centerInParent ()
  {
    Container parent = this.getParent();
    Point topLeft = parent.getLocationOnScreen();

    Dimension parentSize = parent.getSize();
    Dimension ownSize = this.getSize();

    int x = ((parentSize.width - ownSize.width)/2) + topLeft.x;
    int y = ((parentSize.height - ownSize.height)/2) + topLeft.y;

    setLocation(x, y);
    requestFocus();
  }

  protected void init() {
    ok.setPreferredSize(bsize);
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ok();
      }
    });

    ok.setMnemonic(13);

    close.setPreferredSize(bsize);
    close.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         exit();
       }
     });
  }

  protected abstract void createComponents();
}
