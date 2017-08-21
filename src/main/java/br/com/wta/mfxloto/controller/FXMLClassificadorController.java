/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.wta.mfxloto.controller;

import java.awt.BorderLayout;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import javax.swing.JFrame;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

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
    @FXML
    private Button btnTreinar;
    @FXML
    private WebView wvWeka;
    @FXML
    private MenuItem miROC;
    @FXML
    private TextArea txtAreaMatrix;
    @FXML
    private TextArea txtAreaClassDetails;
    @FXML
    private TextArea txtAreaCumulativeMarginDistribution;
    @FXML
    private Pagination pgResultado;

    @FXML
    protected void abrirARFF(ActionEvent event) {
        try {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Abrir ARFF");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ARFF File", "*.arff")
            );
            selectFileARFF = fileChooser.showOpenDialog(AnchorPane.getScene().getWindow());

            if (selectFileARFF != null) {
                setSource(new DataSource(selectFileARFF.getAbsolutePath()));
                setScheme(new Instances(getSource().getDataSet()));

                if (getScheme().classIndex() == -1) {
                    getScheme().setClassIndex(getScheme().numAttributes() - 1);
                }

                slFolder.setMax(getScheme().numInstances());
                slFolder.setMajorTickUnit(getScheme().numInstances() / 2);

                gerarTabela(data, tableViewAmostra);
                gridMain.setDisable(false);
            }
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
                    miROC.setDisable(true);
                    btnTreinar.setDisable(true);
                    btnFicarMilionario.setDisable(true);
                    pgResultado.setDisable(true);
                    slFolder.setDisable(true);
                    slSeed.setDisable(true);
                    txtSeed.setDisable(true);
                    txtFolder.setDisable(true);
                    setMlp(new MultilayerPerceptron());
                    getMlp().buildClassifier(getScheme());
                    criarEvaluations(Integer.parseInt(txtFolder.getText()), Integer.parseInt(txtSeed.getText()));
                    setSchemeDense(new DenseInstance(getScheme().numAttributes()));
                    getSchemeDense().setDataset(getScheme());
                    txtCorreto.setText(String.valueOf(getEval().correct()));
                    txtIncorreto.setText(String.valueOf(getEval().incorrect()));
                    txtKappa.setText(String.valueOf(getEval().kappa()));
                    txtMeanAbsoluteError.setText(String.valueOf(getEval().meanAbsoluteError()));
                    txtRootMeanSquaredError.setText(String.valueOf(getEval().rootMeanSquaredError()));
                    txtRootRelativeSquaredError.setText(String.valueOf(getEval().rootRelativeSquaredError()));
                    txtRelativeAbsoluteError.setText(String.valueOf(getEval().relativeAbsoluteError()));
                    txtNumInstances.setText(String.valueOf(getEval().numInstances()));
                    txtAreaMatrix.setText(getEval().toMatrixString());
                    txtAreaClassDetails.setText(getEval().toClassDetailsString());
                    txtAreaCumulativeMarginDistribution.setText(getEval().toCumulativeMarginDistributionString());
                    txtSeed.setDisable(false);
                    txtFolder.setDisable(false);
                    slFolder.setDisable(false);
                    slSeed.setDisable(false);
                    btnFicarMilionario.setDisable(false);
                    btnTreinar.setDisable(false);
                    miROC.setDisable(false);
                    updateProgress(0, 0);
                    return null;
                }
            };
            pbTreino.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        } catch (Exception ex) {
            Logger.getLogger(FXMLClassificadorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void gerarCurva(ActionEvent event) {
        try {
            ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0;
            Instances curve = tc.getCurve(getEval().predictions(), classIndex);
            PlotData2D plotdata = new PlotData2D(curve);
            plotdata.setPlotName(curve.relationName());
            plotdata.addInstanceNumberAttribute();
            ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
            tvp.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(curve), 4) + ")");
            tvp.setName(curve.relationName());
            tvp.addPlot(plotdata);
            final JFrame jf = new JFrame("WEKA ROC: " + tvp.getName());
            jf.setSize(500, 400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(tvp, BorderLayout.CENTER);
            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jf.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(FXMLClassificadorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void sortearDezenas(ActionEvent event) {
        setDataLength(Integer.parseInt(txtQuantidadeJogos.getText()));

        if (getDataLength() % rowsPerPage == 0) {
            pgResultado.setPageCount(getDataLength() / rowsPerPage);
        } else {
            pgResultado.setPageCount(getDataLength() / rowsPerPage + 1);
        }

        Task<Void> runFactoryTask;
        try {
            runFactoryTask = runFactory();
            runFactoryTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent t) {
                    // Code to run once runFactory() is completed **successfully**
                    pgResultado.setPageFactory(new Callback<Integer, Node>() {
                        public Node call(Integer pageIndex) {
                            int fromIndex = pageIndex * rowsPerPage;
                            int toIndex = Math.min(fromIndex + rowsPerPage, dataResultado.size());
                            List<CartelaClassificada> cartelas = new ArrayList<>(dataResultado);
                            tabelaResultado.setItems(FXCollections.observableList(cartelas.subList(fromIndex, toIndex)));
                            pgResultado.setDisable(false);
                            btnTreinar.setDisable(false);
                            return new BorderPane(tabelaResultado);
                        }
                    });
                }
            });
            pbTreino.progressProperty().bind(runFactoryTask.progressProperty());
            new Thread(runFactoryTask).start();
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLClassificadorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Task<Void> runFactory() throws InterruptedException {
        return new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                btnTreinar.setDisable(true);
                pgResultado.setDisable(true);
                dataResultado = criarDados();
                updateProgress(0, 0);
                return null;
            }
        };
    }

    private synchronized Collection<CartelaClassificada> criarDados() {
        // TODO Auto-generated method stub
        int size = Integer.parseInt(txtQuantidadeJogos.getText());
        Collection<CartelaClassificada> dataList = new TreeSet<CartelaClassificada>();

        for (int i = 0; i < size; i++) {
            char[] cartela = new char[]{
                '?', '?', '?', '?', '?', '?', '?', '?', '?', '?',
                '?', '?', '?', '?', '?', '?', '?', '?', '?', '?',
                '?', '?', '?', '?', '?', '?', '?', '?', '?', '?',
                '?', '?', '?', '?', '?', '?', '?', '?', '?', '?',
                '?', '?', '?', '?', '?', '?', '?', '?', '?', '?',
                '?', '?', '?', '?', '?', '?', '?', '?', '?', '?'
            };
            int[] dezenas = new int[6];
            int idezenas = 0;

            Random rng = new Random();
            TreeSet<Integer> generated = new TreeSet<>();
            while (generated.size() < 6) {
                Integer next = rng.nextInt(60) + 1;
                cartela[next - 1] = 't';
                generated.add(next);
            }

            Set<Integer> escolhidos = generated;
            Iterator<Integer> it = escolhidos.iterator();
            while (it.hasNext()) {
                dezenas[idezenas] = it.next();
                idezenas++;
            }

            for (int k = 0; k < cartela.length; k++) {
                getSchemeDense().setValue(k, cartela[k]);
            }

            try {
                setProbabilidade(getMlp().distributionForInstance(getSchemeDense()));
            } catch (Exception ex) {
                Logger.getLogger(FXMLClassificadorController.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataList.add(new CartelaClassificada(dezenas[0], dezenas[1], dezenas[2], dezenas[3], dezenas[4], dezenas[5], getProbabilidade()[AC], getProbabilidade()[NA]));
        }
        return dataList;
    }

    private synchronized TableView<CartelaClassificada> criarTabela() {
        TableView<CartelaClassificada> tableViewResultado = new TableView<>();
        tableViewResultado.getColumns().clear();

        TableColumn<CartelaClassificada, Integer> dezena_1 = new TableColumn("Dezena 1");
        dezena_1.setCellValueFactory(new PropertyValueFactory<CartelaClassificada, Integer>("d1"));
        dezena_1.prefWidthProperty().bind(tableViewResultado.widthProperty().divide(8));

        TableColumn<CartelaClassificada, Integer> dezena_2 = new TableColumn("Dezena 2");
        dezena_2.setCellValueFactory(new PropertyValueFactory<CartelaClassificada, Integer>("d2"));
        dezena_2.prefWidthProperty().bind(tableViewResultado.widthProperty().divide(8));

        TableColumn<CartelaClassificada, Integer> dezena_3 = new TableColumn("Dezena 3");
        dezena_3.setCellValueFactory(new PropertyValueFactory<CartelaClassificada, Integer>("d3"));
        dezena_3.prefWidthProperty().bind(tableViewResultado.widthProperty().divide(8));

        TableColumn<CartelaClassificada, Integer> dezena_4 = new TableColumn("Dezena 4");
        dezena_4.setCellValueFactory(new PropertyValueFactory<CartelaClassificada, Integer>("d4"));
        dezena_4.prefWidthProperty().bind(tableViewResultado.widthProperty().divide(8));

        TableColumn<CartelaClassificada, Integer> dezena_5 = new TableColumn("Dezena 5");
        dezena_5.setCellValueFactory(new PropertyValueFactory<CartelaClassificada, Integer>("d5"));
        dezena_5.prefWidthProperty().bind(tableViewResultado.widthProperty().divide(8));

        TableColumn<CartelaClassificada, Integer> dezena_6 = new TableColumn("Dezena 6");
        dezena_6.setCellValueFactory(new PropertyValueFactory<CartelaClassificada, Integer>("d6"));
        dezena_6.prefWidthProperty().bind(tableViewResultado.widthProperty().divide(8));

        TableColumn<CartelaClassificada, Double> probabilidadeAC = new TableColumn("AC");
        probabilidadeAC.setCellValueFactory(new PropertyValueFactory<CartelaClassificada, Double>("AC"));
        probabilidadeAC.prefWidthProperty().bind(tableViewResultado.widthProperty().divide(8));

        TableColumn<CartelaClassificada, Double> probabilidadeNA = new TableColumn("NA");
        probabilidadeNA.setCellValueFactory(new PropertyValueFactory<CartelaClassificada, Double>("NA"));
        probabilidadeNA.prefWidthProperty().bind(tableViewResultado.widthProperty().divide(8));

        tableViewResultado.setScaleShape(true);
        tableViewResultado.getColumns().addAll(dezena_1, dezena_2, dezena_3, dezena_4, dezena_5, dezena_6, probabilidadeAC, probabilidadeNA);
        return tableViewResultado;
    }

    private void criarEvaluations(int folder, int seed) throws Exception {
        // TODO Auto-generated method stub
        setEval(new Evaluation(getScheme()));
        getEval().crossValidateModel(getMlp(), getScheme(), folder, new Random(seed));
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
            if (getScheme().attribute(i).isNumeric()) {
                root[i].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, Number>, ObservableValue<Number>>() {
                    @Override
                    public ObservableValue<Number> call(TableColumn.CellDataFeatures<ObservableList, Number> param) {
                        return new SimpleIntegerProperty(Integer.parseInt(param.getValue().get(j).toString()));
                    }
                });
            } else if (getScheme().attribute(i).isNominal()) {
                root[i].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
            }
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

            pgResultado.setDisable(true);
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

            TextFormatter formatterTotal = new TextFormatter(new NumberStringConverter(decimalFormatUnidade));
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

            pgResultado.setPageFactory(new Callback<Integer, Node>() {
                public Node call(Integer pageIndex) {
                    tabelaResultado = criarTabela();
                    return new BorderPane(tabelaResultado);
                }
            });

            WebEngine engine = wvWeka.getEngine();
            engine.load("http://loterias.caixa.gov.br/wps/portal/loterias/landing/megasena/");
        } catch (Exception ex) {
            Logger.getLogger(FXMLClassificadorController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static class CartelaClassificada implements Comparable {

        private final SimpleIntegerProperty d1;
        private final SimpleIntegerProperty d2;
        private final SimpleIntegerProperty d3;
        private final SimpleIntegerProperty d4;
        private final SimpleIntegerProperty d5;
        private final SimpleIntegerProperty d6;
        private final SimpleDoubleProperty AC;
        private final SimpleDoubleProperty NA;

        private CartelaClassificada(Integer d1, Integer d2, Integer d3, Integer d4, Integer d5, Integer d6, Double AC, Double NA) {
            this.d1 = new SimpleIntegerProperty(d1);
            this.d2 = new SimpleIntegerProperty(d2);
            this.d3 = new SimpleIntegerProperty(d3);
            this.d4 = new SimpleIntegerProperty(d4);
            this.d5 = new SimpleIntegerProperty(d5);
            this.d6 = new SimpleIntegerProperty(d6);
            this.AC = new SimpleDoubleProperty(AC);
            this.NA = new SimpleDoubleProperty(NA);
        }

        public Integer getD1() {
            return d1.get();
        }

        public Integer getD2() {
            return d2.get();
        }

        public Integer getD3() {
            return d3.get();
        }

        public Integer getD4() {
            return d4.get();
        }

        public Integer getD5() {
            return d5.get();
        }

        public Integer getD6() {
            return d6.get();
        }

        public Double getAC() {
            return AC.get();
        }

        public Double getNA() {
            return NA.get();
        }

        public void setD1(Integer value) {
            d1.set(value);
        }

        public void setD2(Integer value) {
            d2.set(value);
        }

        public void setD3(Integer value) {
            d3.set(value);
        }

        public void setD4(Integer value) {
            d4.set(value);
        }

        public void setD5(Integer value) {
            d5.set(value);
        }

        public void setD6(Integer value) {
            d6.set(value);
        }

        public void setAC(Double value) {
            AC.set(value);
        }

        public void setNA(Double value) {
            NA.set(value);
        }

        @Override
        public int compareTo(Object o) {
            if (!(o instanceof CartelaClassificada)) {
                throw new IllegalArgumentException("Parameter must be a Cartela");
            }
            CartelaClassificada temp = (CartelaClassificada) o;
            if (temp.getNA() < this.getNA()) {
                return -1;
            } else if (temp.getNA() > this.getNA()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static class Cartela implements Comparable {

        private Integer[] dezenas = new Integer[6];

        private Cartela(Integer[] dezenas) {
            this.dezenas = dezenas;
        }

        public Integer[] getDezenas() {
            return dezenas;
        }

        public void setDezenas(Integer[] dezenas) {
            this.dezenas = dezenas;
        }

        @Override
        public int compareTo(Object o) {
            if (!(o instanceof Cartela)) {
                throw new IllegalArgumentException("Parameter must be a Cartela");
            }
            Cartela temp = (Cartela) o;
            if (temp.getDezenas()[0] == this.getDezenas()[0]
                    && temp.getDezenas()[1] == this.getDezenas()[1]
                    && temp.getDezenas()[2] == this.getDezenas()[2]
                    && temp.getDezenas()[3] == this.getDezenas()[3]
                    && temp.getDezenas()[4] == this.getDezenas()[4]
                    && temp.getDezenas()[5] == this.getDezenas()[5]) {
                return 0;
            } else if (temp.getDezenas()[0] < this.getDezenas()[0]
                    || temp.getDezenas()[1] < this.getDezenas()[1]
                    || temp.getDezenas()[2] < this.getDezenas()[2]
                    || temp.getDezenas()[3] < this.getDezenas()[3]
                    || temp.getDezenas()[4] < this.getDezenas()[4]
                    || temp.getDezenas()[5] < this.getDezenas()[5]) {
                return 1;
            } else {
                return -1;
            }
        }
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

    public MultilayerPerceptron getMlp() {
        return mlp;
    }

    public void setMlp(MultilayerPerceptron mlp) {
        this.mlp = mlp;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    private File selectFileARFF;
    private final ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private String options;
    private DataSource source;
    private Instances scheme;
    private DenseInstance schemeDense;
    private Evaluation eval;
    private double[] probabilidade;
    private MultilayerPerceptron mlp;
    private FileChooser fileChooser;
    private final int NA = 0;
    private final int AC = 1;
    private int dataLength = 0;
    private final static int rowsPerPage = 50;
    private TableView<CartelaClassificada> tabelaResultado;
    private Collection<CartelaClassificada> dataResultado;
}
