/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.wta.mfxloto.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import br.com.wta.mfxloto.model.DefaultResources;
import javafx.scene.Node;
import javafx.stage.Modality;

/**
 * FXML Controller class
 *
 * @author w_tel
 */
public class FXMLMainLotoController implements Initializable {

    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private TableView tableViewARFF;
    @FXML
    private MenuItem miConfig;

    private FileChooser fileChooser;
    private File selectFileARFF;
    private Instances dataset;
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private DefaultResources myResources;

    @FXML
    protected void configClassificador(ActionEvent event) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Classificador");
            stage.setResizable(true);
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("/fxml/FXMLClassificador.fxml"));
            myResources = new DefaultResources(dataset, stage);
            root.setResources(myResources);
            Scene scene = new Scene(root.load());
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLMainLotoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void abrirARFF(ActionEvent event) {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir ARFF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ARFF File", "*.arff")
        );
        selectFileARFF = fileChooser.showOpenDialog(AnchorPane.getScene().getWindow());
        miConfig.setDisable(false);
        try {
            gerarTabela(selectFileARFF, data, tableViewARFF);
        } catch (Exception ex) {
            Logger.getLogger(FXMLMainLotoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void gerarTabela(File path, ObservableList<ObservableList> data, TableView table) throws Exception {
        if (path != null) {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(path.getAbsolutePath());
            dataset = source.getDataSet();
            TableColumn[] root = new TableColumn[dataset.numAttributes()];
            table.getColumns().clear();
            data.clear();
            for (int i = 0; i < dataset.numAttributes(); i++) {
                final int j = i;
                root[i] = new TableColumn(dataset.attribute(i).name());
                root[i].setMinWidth(100);
                root[i].setMinWidth(100);
                root[i].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                table.getColumns().add(root[i]);
            }
            for (int i = 0; i < dataset.numInstances(); i++) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int j = 0; j < dataset.numAttributes(); j++) {
                    row.add(dataset.instance(i).toString(j));
                }
                data.add(row);
            }
            table.setItems(data);
        }
    }

    public Instances getDataset() {
        return dataset;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        miConfig.setDisable(true);
    }
}
