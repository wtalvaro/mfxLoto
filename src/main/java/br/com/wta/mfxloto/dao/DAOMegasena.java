/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.wta.mfxloto.dao;

import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author w_tel
 */
public class DAOMegasena {
    
    private final SimpleIntegerProperty d1;
    private final SimpleIntegerProperty d2;
    private final SimpleIntegerProperty d3;
    private final SimpleIntegerProperty d4;
    private final SimpleIntegerProperty d5;
    private final SimpleIntegerProperty d6;
    
    public DAOMegasena(int d1, int d2, int d3, int d4, int d5, int d6) {
        this.d1 = new SimpleIntegerProperty(d1);
        this.d2 = new SimpleIntegerProperty(d2);
        this.d3 = new SimpleIntegerProperty(d3);
        this.d4 = new SimpleIntegerProperty(d4);
        this.d5 = new SimpleIntegerProperty(d5);
        this.d6 = new SimpleIntegerProperty(d6);
    }
    
    public SimpleIntegerProperty getD1() {
        return d1;
    }
    
    public SimpleIntegerProperty getD2() {
        return d2;
    }
    
    public SimpleIntegerProperty getD3() {
        return d3;
    }
    
    public SimpleIntegerProperty getD4() {
        return d4;
    }
    
    public SimpleIntegerProperty getD5() {
        return d5;
    }
    
    public SimpleIntegerProperty getD6() {
        return d6;
    }
    
    public void setD1(int value) {
        d1.set(value);
    }
    
    public void setD2(int value) {
        d2.set(value);
    }
    
    public void setD3(int value) {
        d3.set(value);
    }
    
    public void setD4(int value) {
        d4.set(value);
    }
    
    public void setD5(int value) {
        d5.set(value);
    }
    
    public void setD6(int value) {
        d6.set(value);
    }
}
