/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.wta.mfxloto.model;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;
import javafx.stage.Stage;
import weka.core.Instances;

/**
 *
 * @author w_tel
 */
public class DefaultResources extends ResourceBundle {

    private Instances dataset;
    private Stage stage;
    private String classificador;
    private String folder;
    private String seed;

    public DefaultResources(Instances dataset, Stage stage) {
        super();
        this.dataset = dataset;
        this.stage = stage;
    }

    public String getClassificador() {
        return classificador;
    }

    public void setClassificador(String classificador) {
        this.classificador = classificador;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    protected Object handleGetObject(String key) {
        if (key.equals("Instances")) {
            return dataset;
        }
        if (key.equals("Stage")) {
            return stage;
        }
        if (key.equals("Classificador")) {
            return classificador;
        }
        if (key.equals("Folder")) {
            return folder;
        }
        if (key.equals("Seed")) {
            return seed;
        }
        return null;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }
}
