/*
 *  last change 2003-09-12 
 */

package com.sapportals.utilities.analyzer.interfaces;

//import com.sapportals.utilities.analyzer.components.*;
import java.util.*;

public interface IPluginAction {
    public void addMessage(String status);
    public void addResult(  int result,
                            boolean fixable,
                            String description,
                            Vector rowData,
                            Vector additionalData);

    public void addResult(int result,boolean fixable,String description);
}