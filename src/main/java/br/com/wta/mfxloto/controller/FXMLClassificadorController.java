/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.wta.mfxloto.controller;

import br.com.wta.mfxloto.dao.DAOMegasena;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

/**
 * FXML Controller class
 *
 * @author w_tel
 */
public class FXMLClassificadorController implements Initializable {

    @FXML
    private ChoiceBox cbClassificador;
    @FXML
    private Slider slFolder;
    @FXML
    private Slider slSeed;
    @FXML
    private TextField txtFolder;
    @FXML
    private TextField txtSeed;
    @FXML
    private TextField txtCorreto;
    @FXML
    private TextField txtIncorreto;
    @FXML
    private TextField txtKappa;
    @FXML
    private TextField txtMeanAbsoluteError;
    @FXML
    private TextField txtRootMeanSquaredError;
    @FXML
    private TextField txtRelativeAbsoluteError;
    @FXML
    private TextField txtRootRelativeSquaredError;
    @FXML
    private TextField txtNumInstances;
    @FXML
    private TableView tableViewResultado;
    @FXML
    private TableView tableViewAmostra;
    @FXML
    private ProgressBar pbTreino;
    @FXML
    private TextField txtApostaUnidade;
    @FXML
    private TextField txtQuantidadeJogos;
    @FXML
    private TextField txtTotalPagar;
    @FXML
    private AnchorPane AnchorPane;
    @FXML
    private GridPane gridMain;
    @FXML
    private Button btnFicarMilionario;

    private File selectFileARFF;
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private String options;
    private DataSource source;
    private Instances scheme;
    private DenseInstance schemeDense;
    private Evaluation eval;
    private double[] probabilidade;
    private J48 j48;
    private FileChooser fileChooser;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            gridMain.setDisable(true);
            cbClassificador.setTooltip(new Tooltip("Selecionar o classificador"));

            DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            decimalFormat.setMaximumFractionDigits(0);

            DecimalFormat decimalFormatUnidade = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            decimalFormatUnidade.setMaximumFractionDigits(2);

            TextFormatter formatterFolder = new TextFormatter(new NumberStringConverter(decimalFormat));
            txtFolder.setTextFormatter(formatterFolder);
            txtFolder.setText(Double.toString(slFolder.getValue()));

            TextFormatter formatterSeed = new TextFormatter(new NumberStringConverter(decimalFormat));
            txtSeed.setTextFormatter(formatterSeed);
            txtSeed.setText(Double.toString(slSeed.getValue()));

            TextFormatter formatterUnitario = new TextFormatter(new NumberStringConverter(decimalFormatUnidade));
            txtApostaUnidade.setTextFormatter(formatterUnitario);
            txtApostaUnidade.setText("3.5");

            TextFormatter formatterQuantidade = new TextFormatter(new NumberStringConverter(decimalFormat));
            txtQuantidadeJogos.setTextFormatter(formatterQuantidade);
            txtQuantidadeJogos.setText("0");

            TextFormatter formatterTotal = new TextFormatter(new NumberStringConverter(decimalFormat));
            txtTotalPagar.setTextFormatter(formatterTotal);
            txtTotalPagar.setText("0");

            slFolder.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    txtFolder.setText(String.valueOf(newValue));
                }
            });

            txtFolder.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    double value = Double.parseDouble(newValue);
                    slFolder.setValue(value);
                }
            });

            slSeed.setValue(1);
            slSeed.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    txtSeed.setText(String.valueOf(newValue));
                }
            });

            txtSeed.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    double value = Double.parseDouble(newValue);
                    slSeed.setValue(value);
                }
            });

            txtApostaUnidade.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!txtApostaUnidade.getText().trim().isEmpty()) {
                        double valor1 = Double.parseDouble(newValue);
                        double valor2 = Double.parseDouble(txtQuantidadeJogos.getText());
                        double resultado = valor1 * valor2;
                        txtTotalPagar.setText(String.valueOf(resultado));
                    }
                }
            });

            txtQuantidadeJogos.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!txtQuantidadeJogos.getText().trim().isEmpty()) {
                        double valor1 = Double.parseDouble(newValue);
                        double valor2 = Double.parseDouble(txtApostaUnidade.getText());
                        double resultado = valor1 * valor2;
                        txtTotalPagar.setText(String.valueOf(resultado));
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(FXMLClassificadorController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void abrirARFF(ActionEvent event) {
        try {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Abrir ARFF");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ARFF File", "*.arff")
            );
            selectFileARFF = fileChooser.showOpenDialog(AnchorPane.getScene().getWindow());

            setSource(new DataSource(selectFileARFF.getAbsolutePath()));
            setScheme(new Instances(getSource().getDataSet()));

            if (getScheme().classIndex() == -1) {
                getScheme().setClassIndex(getScheme().numAttributes() - 1);
            }

            slFolder.setMax(getScheme().numInstances());
            slFolder.setMajorTickUnit(getScheme().numInstances() / 2);

            gerarTabela(data, tableViewAmostra);
            gridMain.setDisable(false);
        } catch (Exception ex) {
            Logger.getLogger(FXMLClassificadorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void executarTreinamentoRede(ActionEvent event) {
        try {
            // TODO Auto-generated method stub
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    setJ48(new J48());
                    getJ48().buildClassifier(getScheme());
                    criarEvaluations(Integer.parseInt(txtFolder.getText()), Integer.parseInt(txtSeed.getText()));
                    txtCorreto.setText(String.valueOf(getEval().correct()));
                    txtIncorreto.setText(String.valueOf(getEval().incorrect()));
                    txtKappa.setText(String.valueOf(getEval().kappa()));
                    txtMeanAbsoluteError.setText(String.valueOf(getEval().meanAbsoluteError()));
                    txtRootMeanSquaredError.setText(String.valueOf(getEval().rootMeanSquaredError()));
                    txtRootRelativeSquaredError.setText(String.valueOf(getEval().rootRelativeSquaredError()));
                    txtRelativeAbsoluteError.setText(String.valueOf(getEval().relativeAbsoluteError()));
                    txtNumInstances.setText(String.valueOf(getEval().numInstances()));
                    updateProgress(0, 0);
                    return null;
                }
            };
            pbTreino.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
            btnFicarMilionario.setDisable(false);
        } catch (Exception ex) {
            Logger.getLogger(FXMLClassificadorController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void sortearDezenas(ActionEvent event) {
        int size = Integer.parseInt(txtQuantidadeJogos.getText());
        ObservableList<ObservableList> dataM = FXCollections.observableArrayList();

        setSchemeDense(new DenseInstance(getScheme().numAttributes()));
        getSchemeDense().setDataset(getScheme());
        dataM.clear();

        for (int k = 0; k < size; k++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            int attributeNext = 0;
            Random rng = new Random();
            TreeSet<Integer> generated = new TreeSet<Integer>();

            while (generated.size() < 6) {
                Integer next = rng.nextInt(60) + 1;
                generated.add(next);
            }

            Set<Integer> escolhidos = generated;
            Iterator<Integer> it = escolhidos.iterator();
            while (it.hasNext()) {
                int number = Integer.parseInt(it.next().toString());
                getSchemeDense().setValue(attributeNext, number);
                row.add(String.valueOf(number));
                attributeNext++;
            }

            try {
                setProbabilidade(getJ48().distributionForInstance(getSchemeDense()));
            } catch (Exception ex) {
                Logger.getLogger(FXMLClassificadorController.class.getName()).log(Level.SEVERE, null, ex);
            }
            row.add(String.valueOf(getProbabilidade()[0]));
            dataM.add(row);
        }

        tableViewResultado.getColumns().clear();
        TableColumn[] root = new TableColumn[getSchemeDense().numAttributes()];

        for (int i = 0; i < getSchemeDense().numAttributes(); i++) {
            final int j = i;
            root[i] = new TableColumn(getSchemeDense().attribute(i).name());
            root[i].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            tableViewResultado.getColumns().add(root[i]);
        }

        tableViewResultado.setItems(dataM);
    }

    private void criarEvaluations(int folder, int seed) throws Exception {
        // TODO Auto-generated method stub
        setEval(new Evaluation(getScheme()));
        getEval().crossValidateModel(getJ48(), getScheme(), folder, new Random(seed));
    }

    private void gerarTabela(ObservableList<ObservableList> data, TableView table) throws Exception {
        TableColumn[] root = new TableColumn[getScheme().numAttributes()];
        table.getColumns().clear();
        data.clear();
        for (int i = 0; i < getScheme().numAttributes(); i++) {
            final int j = i;
            root[i] = new TableColumn(getScheme().attribute(i).name());
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
        for (int i = 0; i < getScheme().numInstances(); i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int j = 0; j < getScheme().numAttributes(); j++) {
                row.add(getScheme().instance(i).toString(j));
            }
            data.add(row);
        }
        table.setItems(data);
    }

    public Slider getSlFolder() {
        return slFolder;
    }

    public Slider getSlSeed() {
        return slSeed;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public DataSource getSource() {
        return source;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    public Instances getScheme() {
        return scheme;
    }

    public void setScheme(Instances scheme) {
        this.scheme = scheme;
    }

    public DenseInstance getSchemeDense() {
        return schemeDense;
    }

    public void setSchemeDense(DenseInstance schemeDense) {
        this.schemeDense = schemeDense;
    }

    public Evaluation getEval() {
        return eval;
    }

    public void setEval(Evaluation eval) {
        this.eval = eval;
    }

    public double[] getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(double[] probabilidade) {
        this.probabilidade = probabilidade;
    }

    public J48 getJ48() {
        return j48;
    }

    public void setJ48(J48 j48) {
        this.j48 = j48;
    }
}
