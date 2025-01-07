/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sap.engine.services.httpserver.server.rcm;

import com.sap.engine.lib.rcm.Resource;

/**
 *
 * @author I024157
 */
public class ThreadResource implements Resource {

    String name;
    int quantity;

    public ThreadResource(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
       
    void setTotalQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }
    
    public String getName() {
        return name;
    }

    public long getTotalQuantity() {
        return quantity;
    }

    public String getUnitName() {
        return "thr";
    }

    public boolean isDisposable() {
        return true;
    }

    public boolean isUnbounded() {
        return true;
    }

}
