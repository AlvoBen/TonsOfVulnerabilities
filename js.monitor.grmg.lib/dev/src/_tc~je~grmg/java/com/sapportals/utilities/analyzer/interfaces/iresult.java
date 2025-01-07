/*
 *  last change 2003-09-12 
 */

package com.sapportals.utilities.analyzer.interfaces;

import java.util.*;

public interface IResult {
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = 1;
    public static final int RESULT_OK_BUT_DONT_CONTINUE_TO_DEPENDENCIES = 2;
    public void setSuccess(int mode);
    public int isSuccess();
    public Vector getRowData();
    public void setRowData(Vector rowData);
    public Vector getAdditionalData();
    public String getDescription();
    public void setDescription(String description);
    public boolean isFixable();
    public void setFixable(boolean b);
    public String toString();
    public boolean equals(Object result);
    public boolean equalsWithoutSuccess(IResult result);
}