package com.sap.engine.lib.xml.names;

import com.sap.engine.lib.log.LogWriter;

/**
 * Calculates "minimum edit distance" between two Strings.
 * Valid editing operations are:
 *    - insert  a character
 *    - delete  a character
 *    - replace a character
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      October 2001
 */
public final class EditDistance {

  private int[] d = new int[1024];
  private int la, lb, la1, lb1;

  private int index(int i, int j) {
    return i * lb1 + j;
  }

  public void printTable() {
    for (int i = 0; i <= la; i++) {
      for (int j = 0; j <= lb; j++) {
        LogWriter.getSystemLogWriter().print(" " + d[index(i, j)]); //$JL-SYS_OUT_ERR$
      } 

      LogWriter.getSystemLogWriter().println("\n"); //$JL-SYS_OUT_ERR$
    } 

    LogWriter.getSystemLogWriter().println("\n"); //$JL-SYS_OUT_ERR$
  }

  public int between(String a, String b) {
    la = a.length();
    lb = b.length();

    if (la == 0) {
      return lb;
    }

    if (lb == 0) {
      return la;
    }

    la1 = la + 1;
    lb1 = lb + 1;
    int l = la1 * lb1;

    if (d.length < l) {
      d = new int[l];
    }

    for (int i = 1; i <= la; i++) {
      d[index(i, 0)] = 1;
    } 

    for (int j = 1; j <= lb; j++) {
      d[index(0, j)] = 1;
    } 

    d[index(0, 0)] = 0;

    for (int i = 1; i <= la; i++) {
      char cha = a.charAt(i - 1);

      for (int j = 1; j <= lb; j++) {
        char chb = b.charAt(j - 1);
        int x = Math.min(d[index(i - 1, j)], d[index(i, j - 1)]) + 1;
        x = Math.min(x, d[index(i - 1, j - 1)] + ((cha == chb) ? 0 : 1));
        d[index(i, j)] = x;
      } 
    } 

    return d[index(la, lb)];
  }

  public static String getMostProbable(String misspelt, String[] dictionary, int delta) {
    try {
      EditDistance editDistance = new EditDistance();
      int indexOfMostProbable = -1;
      int distanceToMostProbable = Integer.MAX_VALUE;

      for (int i = 0; i < dictionary.length; i++) {
        int d = editDistance.between(misspelt, dictionary[i]);

        if (d < distanceToMostProbable) {
          indexOfMostProbable = i;
          distanceToMostProbable = d;
        }
      } 

      return ((indexOfMostProbable != -1) && (distanceToMostProbable <= delta)) ? dictionary[indexOfMostProbable] : null;
    } catch (Exception e) {
      //$JL-EXC$

      // This can't happen but I really gotto be sure nothing will fail that way...
      return null;
    }
  }

//  public static void main(String[] args) throws Exception {
//    String a = "alabala";
//    String b = "cabala";
//    EditDistance ed = new EditDistance();
//    LogWriter.getSystemLogWriter().println("Distance between '" + a + "' and '" + b + "' = " + ed.between(a, b));
//    ed.printTable();
//  }

}

