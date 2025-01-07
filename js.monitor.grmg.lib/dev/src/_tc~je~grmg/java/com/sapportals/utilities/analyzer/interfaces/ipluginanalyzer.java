/*
 *  last change 2003-09-12 
 */

package com.sapportals.utilities.analyzer.interfaces;

import java.util.*;

public interface IPluginAnalyzer {
    public String getDescription();
    public void analyze(IPluginAction e);
    public void fix(IResult result);
    public Vector getColumnNames();
}