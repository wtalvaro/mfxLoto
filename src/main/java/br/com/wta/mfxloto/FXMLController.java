package br.com.wta.mfxloto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils;

public class FXMLController implements Initializable {

    @FXML
    private ListView listARFF;
    @FXML
    private ListView listCSV;
    @FXML
    private ListView listURL;
    @FXML
    private ListView listDB;
    @FXML
    private ListView listFilterSupAttr;
    @FXML
    private ListView listFilterSupInst;
    @FXML
    private ListView listFilterNSupAttr;
    @FXML
    private ListView listFilterNSupInst;
    @FXML
    private TableView tableViewARFF;

    private Integer num = 0;
    private File selectFileARFF;
    private File selectFileCSV;
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private FileChooser fileChooser;
    private Reader in;
    private Dialog dialog;
    private Label labelTitle;
    private TextArea textExpansion;
    private TextField textField;
    private GridPane expContent;
    private GridPane mainContent;
    private Optional<ButtonType> result;
    private ScrollPane scroll;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        List<String> values = Arrays.asList("Abrir ARFF", "Converter para ARFF");
        listARFF.setItems(FXCollections.observableList(values));
        listARFF.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof ListCell) {
                    ListCell cell = (ListCell) eventTarget;
                    switch (cell.getText()) {

                        case "Abrir ARFF":
                            fileChooser = new FileChooser();
                            fileChooser.setTitle("Abrir ARFF");
                            fileChooser.getExtensionFilters().addAll(
                                    new FileChooser.ExtensionFilter("ARFF File", "*.arff")
                            );

                            selectFileARFF = fileChooser.showOpenDialog(listARFF.getScene().getWindow());
                            if (selectFileARFF != null) {
                                try {
                                    ConverterUtils.DataSource source = new ConverterUtils.DataSource(selectFileARFF.getAbsolutePath());
                                    Instances dataset = source.getDataSet();
                                    TableColumn[] root = new TableColumn[dataset.numAttributes()];
                                    tableViewARFF.getColumns().clear();
                                    data.clear();
                                    for (int i = 0; i < dataset.numAttributes(); i++) {
                                        final int j = i;
                                        root[i] = new TableColumn(dataset.attribute(i).name());
                                        root[i].setMinWidth(100);
                                        root[i].setMinWidth(100);
                                        root[i].setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                                            @Override
                                            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                                                return new SimpleStringProperty(param.getValue().get(j).toString());
                                            }
                                        });
                                        tableViewARFF.getColumns().add(root[i]);
                                    }

                                    for (int i = 0; i < dataset.numInstances(); i++) {
                                        ObservableList<String> row = FXCollections.observableArrayList();
                                        for (int j = 0; j < dataset.numAttributes(); j++) {
                                            row.add(dataset.instance(i).toString(j));
                                        }
                                        data.add(row);
                                    }

                                    tableViewARFF.setItems(data);
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (Exception ex) {
                                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;

                        case "Converter para ARFF":
                            fileChooser = new FileChooser();
                            fileChooser.setTitle("Salvar ARFF");
                            fileChooser.getExtensionFilters().addAll(
                                    new FileChooser.ExtensionFilter("ARFF File", "*.arff")
                            );

                            selectFileARFF = fileChooser.showSaveDialog(listARFF.getScene().getWindow());
                            if (selectFileCSV != null) {
                                //Load CSV File
                                CSVLoader loader = new CSVLoader();
                                Instances dataset = null;

                                try {
                                    loader.setSource(new File(selectFileCSV.getAbsolutePath()));
                                    dataset = loader.getDataSet();
                                } catch (IOException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                //Save ARFF File
                                ArffSaver saver = new ArffSaver();
                                saver.setInstances(dataset);
                                try {
                                    saver.setFile(new File(selectFileARFF.getAbsolutePath()));
                                    saver.writeBatch();
                                } catch (IOException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        });

        values = Arrays.asList("Abrir CSV", "Abrir Excel CSV", "Converter para CSV");

        listCSV.setItems(FXCollections.observableList(values));
        listCSV.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event
            ) {
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof ListCell) {
                    ListCell cell = (ListCell) eventTarget;
                    switch (cell.getText()) {

                        case "Abrir CSV": {
                            try {
                                fileChooser = new FileChooser();
                                fileChooser.setTitle("Abrir CSV");
                                fileChooser.getExtensionFilters().addAll(
                                        new FileChooser.ExtensionFilter("CSV File", "*.csv")
                                );
                                selectFileCSV = fileChooser.showOpenDialog(listARFF.getScene().getWindow());
                                if (selectFileCSV != null) {
                                    in = new FileReader(selectFileCSV.getAbsolutePath());
                                    CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT.withFirstRecordAsHeader());
                                    Map<String, Integer> map = parser.getHeaderMap();
                                    TableColumn[] root = new TableColumn[map.entrySet().size()];
                                    tableViewARFF.getColumns().clear();
                                    data.clear();
                                    int i = 0;
                                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                                        final int j = i;
                                        root[i] = new TableColumn(entry.getKey());
                                        root[i].setMinWidth(100);
                                        root[i].setMinWidth(100);
                                        root[i].setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                                            @Override
                                            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                                                return new SimpleStringProperty(param.getValue().get(j).toString());
                                            }
                                        });
                                        tableViewARFF.getColumns().add(root[i]);
                                        i++;
                                    }

                                    in = new FileReader(selectFileCSV.getAbsolutePath());
                                    Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
                                    for (CSVRecord record : records) {
                                        ObservableList<String> row = FXCollections.observableArrayList();
                                        for (int j = 0; j < map.entrySet().size(); j++) {
                                            row.add(record.get(j));
                                        }
                                        data.add(row);
                                    }
                                    tableViewARFF.setItems(data);
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;

                        case "Abrir Excel CSV":
                            try {
                                fileChooser = new FileChooser();
                                fileChooser.setTitle("Abrir Excel CSV");
                                fileChooser.getExtensionFilters().addAll(
                                        new FileChooser.ExtensionFilter("CSV File", "*.csv")
                                );
                                selectFileCSV = fileChooser.showOpenDialog(listARFF.getScene().getWindow());
                                if (selectFileCSV != null) {
                                    final URL url = selectFileCSV.toURI().toURL();
                                    final Reader reader = new InputStreamReader(new BOMInputStream(url.openStream()), "UTF-8");
                                    CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
                                    Map<String, Integer> map = parser.getHeaderMap();
                                    TableColumn[] root = new TableColumn[map.entrySet().size()];
                                    tableViewARFF.getColumns().clear();
                                    data.clear();
                                    int i = 0;
                                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                                        final int j = i;
                                        root[i] = new TableColumn(entry.getKey());
                                        root[i].setMinWidth(100);
                                        root[i].setMinWidth(100);
                                        root[i].setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                                            @Override
                                            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                                                return new SimpleStringProperty(param.getValue().get(j).toString());
                                            }
                                        });
                                        tableViewARFF.getColumns().add(root[i]);
                                        i++;
                                    }

                                    final Reader readerContent = new InputStreamReader(new BOMInputStream(url.openStream()), "UTF-8");
                                    Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(readerContent);
                                    for (CSVRecord record : records) {
                                        ObservableList<String> row = FXCollections.observableArrayList();
                                        for (int j = 0; j < map.entrySet().size(); j++) {
                                            row.add(record.get(j));
                                        }
                                        data.add(row);
                                    }
                                    tableViewARFF.setItems(data);
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;

                        case "Converter para CSV":
                            fileChooser = new FileChooser();
                            fileChooser.setTitle("Salvar CSV");
                            fileChooser.getExtensionFilters().addAll(
                                    new FileChooser.ExtensionFilter("CSV File", "*.csv")
                            );

                            selectFileCSV = fileChooser.showSaveDialog(listARFF.getScene().getWindow());
                            if (selectFileARFF != null) {
                                ArffLoader loader = new ArffLoader();
                                Instances dataset = null;
                                try {
                                    loader.setSource(new File(selectFileARFF.getAbsolutePath()));
                                    dataset = loader.getDataSet();
                                } catch (IOException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                //Save CSV
                                CSVSaver saver = new CSVSaver();
                                saver.setInstances(dataset);
                                try {
                                    saver.setFile(new File(selectFileCSV.getAbsolutePath()));
                                    saver.writeBatch();
                                } catch (IOException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        }
        );

        values = Arrays.asList("Salvar de URL");

        listURL.setItems(FXCollections.observableList(values));
        listURL.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event
            ) {
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof ListCell) {
                    ListCell cell = (ListCell) eventTarget;
                    switch (cell.getText()) {
                        case "Salvar de URL":
                            dialog = new Dialog();
                            dialog.setTitle("Ler Instâncias");
                            dialog.initStyle(StageStyle.UTILITY);
                            dialog.initOwner(listARFF.getScene().getWindow());
                            dialog.initModality(Modality.WINDOW_MODAL);
                            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                            labelTitle = new Label("Entre com a fonte URL:");

                            textField = new TextField();
                            textField.setMaxWidth(Double.MAX_VALUE);
                            textField.setMaxHeight(Double.MAX_VALUE);
                            GridPane.setVgrow(textField, Priority.ALWAYS);
                            GridPane.setHgrow(textField, Priority.ALWAYS);

                            mainContent = new GridPane();
                            mainContent.setHgap(10);
                            mainContent.setVgap(10);
                            mainContent.setMaxWidth(Double.MAX_VALUE);
                            mainContent.add(labelTitle, 0, 0);
                            mainContent.add(textField, 0, 1);
                            mainContent.setPrefWidth(480);

                            dialog.getDialogPane().setContent(mainContent);
                            result = dialog.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                try {
                                    URL url = new URL(textField.getText());
                                    fileChooser = new FileChooser();
                                    fileChooser.setTitle("Salvar ARFF");
                                    fileChooser.getExtensionFilters().addAll(
                                            new FileChooser.ExtensionFilter("ARFF File", "*.arff")
                                    );

                                    selectFileARFF = fileChooser.showSaveDialog(listARFF.getScene().getWindow());
                                    if (selectFileARFF != null) {
                                        File destination = new File(selectFileARFF.getAbsolutePath());
                                        FileUtils.copyURLToFile(url, destination);
                                    }
                                } catch (MalformedURLException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                System.out.println("OK");
                            } else if (result.get() == ButtonType.CANCEL) {
                                System.out.println("CANCEL");
                            }
                            break;

                        default:
                            break;
                    }
                }
            }

        }
        );

        values = Arrays.asList("HSQL", "MSACCESS", "MSSQLSERVER", "MSSQLSERVER2005", "MySQL", "ODBC", "ORACLE", "POSTGRESQL", "SQLITE3");

        listDB.setItems(FXCollections.observableList(values));
        listDB.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event
            ) {
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof ListCell) {
                    ListCell cell = (ListCell) eventTarget;
                    switch (cell.getText()) {
                        case "Microsoft SQL":

                            break;

                        case "Oracle DB":

                            break;
                        case "IBM DB2":
                            break;

                        case "MySQL":
                            break;

                        case "PostgreSQL":
                            break;

                        case "SQLite":
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        );

        values = Arrays.asList(
                "Add Classification",
                "Attribute Selection",
                "Class Conditional Probabilities",
                "Class Order",
                "Discretize",
                "Merge Nominal Values",
                "Nominal To Binary",
                "Partition MemberShip"
        );

        listFilterSupAttr.setItems(FXCollections.observableList(values));
        listFilterSupAttr.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event
            ) {
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof ListCell) {
                    ListCell cell = (ListCell) eventTarget;
                    switch (cell.getText()) {
                        case "Add Classification":
                            dialog = new Dialog();
                            dialog.setTitle("Add Classification");
                            dialog.initStyle(StageStyle.UTILITY);
                            dialog.initOwner(listARFF.getScene().getWindow());
                            dialog.initModality(Modality.WINDOW_MODAL);
                            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                            labelTitle = new Label("Um filtro para adicionar a classificação, a distribuição de classe e um sinalizador de erro para um conjunto de dados com um classificador.");
                            textExpansion = new TextArea("As opções válidas são:\n"
                                    + " -D\n"
                                    + "  Activa a saída de informações de depuração.\n"
                                    + " \n"
                                    + " -W <classifier specification>\n"
                                    + "  Nome da classe completa do classificador a ser usado, seguido\n"
                                    + "  Por opções de esquema. por exemplo: \"Weka.classifiers.bayes.NaiveBayes -D\"\n"
                                    + "  (Padrão: weka.classifiers.rules.ZeroR)\n"
                                    + " \n"
                                    + " -serialized <arquivo>\n"
                                    + "  Em vez de formar um classificador nos dados, pode-se também fornecer um modelo serializado e use isso para marcar os dados.\n"
                                    + " \n"
                                    + " -classification\n"
                                    + "  Adiciona um atributo com a classificação atual.\n"
                                    + "  (Padrão: desativado)\n"
                                    + " \n"
                                    + " -remove-old-class\n"
                                    + "  Remove o atributo da classe anterior.\n"
                                    + "  (Padrão: desativado)\n"
                                    + " \n"
                                    + " -distribution\n"
                                    + "  Adiciona atributos com a distribuição para todas as classes (Para classes numéricas, isso será idêntico ao atributo saída com '-classificação').\n"
                                    + "  (Padrão: desativado)\n"
                                    + " \n"
                                    + " -error\n"
                                    + "  Adiciona um atributo que indica se a saída do classificador\n"
                                    + "  Uma classificação errada (para classes numéricas é o numérico diferença).\n"
                                    + "  (Padrão: desativado)");

                            textExpansion.setEditable(false);
                            textExpansion.setWrapText(true);
                            textExpansion.setMaxWidth(Double.MAX_VALUE);
                            textExpansion.setMaxHeight(Double.MAX_VALUE);

                            textField = new TextField();
                            textField.setMaxWidth(Double.MAX_VALUE);
                            textField.setMaxHeight(Double.MAX_VALUE);
                            GridPane.setVgrow(textField, Priority.ALWAYS);
                            GridPane.setHgrow(textField, Priority.ALWAYS);
                            GridPane.setVgrow(textExpansion, Priority.ALWAYS);
                            GridPane.setHgrow(textExpansion, Priority.ALWAYS);

                            expContent = new GridPane();
                            expContent.setHgap(10);
                            expContent.setVgap(10);
                            expContent.setMaxWidth(Double.MAX_VALUE);
                            expContent.add(textExpansion, 0, 0);
                            expContent.setPrefHeight(240);

                            mainContent = new GridPane();
                            mainContent.setHgap(10);
                            mainContent.setVgap(10);
                            mainContent.setMaxWidth(Double.MAX_VALUE);
                            mainContent.add(labelTitle, 0, 0);
                            mainContent.add(textField, 0, 1);

                            dialog.getDialogPane().setContent(mainContent);
                            dialog.getDialogPane().setExpandableContent(expContent);
                            result = dialog.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                System.out.println("OK");
                            } else if (result.get() == ButtonType.CANCEL) {
                                System.out.println("CANCEL");
                            }
                            break;

                        case "Attribute Selection":
                            dialog = new Dialog();
                            dialog.setTitle("Attribute Selection");
                            dialog.initStyle(StageStyle.UTILITY);
                            dialog.initOwner(listARFF.getScene().getWindow());
                            dialog.initModality(Modality.WINDOW_MODAL);
                            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                            labelTitle = new Label("Um filtro de atributos supervisionados que pode ser usado para selecionar atributos.");
                            textExpansion = new TextArea("As opções válidas são:\n"
                                    + " -S <\"Nome da classe de pesquisa [opções de pesquisa]\">\n"
                                    + "  Configura o método de busca para avaliadores de subconjuntos.\n"
                                    + "  por exemplo. -S \"weka.attributeSelection.BestFirst -S 8\"\n"
                                    + " \n"
                                    + " -E <\"Nome da classe de avaliação do atributo / subconjunto [opções do avaliador]\">\n"
                                    + "  Define o atributo / avaliador de subconjuntos.\n"
                                    + "  por exemplo. -E \"weka.attributeSelection.CfsSubsetEval -L\"\n"
                                    + " \n"
                                    + " Opções específicas para o avaliador weka.attributeSelection.CfsSubsetEval:\n"
                                    + " -M\n"
                                    + "  Trate os valores em falta como um valor separado.\n"
                                    + " \n"
                                    + " -EU\n"
                                    + "  Não inclua atributos predictivos locais.\n"
                                    + " \n"
                                    + " Opções específicas para pesquisar weka.attributeSelection.BestFirst:\n"
                                    + " -P <start set>\n"
                                    + "  Especifique um conjunto inicial de atributos.\n"
                                    + "  Por exemplo. 1,3,5-7.\n"
                                    + " \n"
                                    + " -D <0 = para trás | 1 = para frente | 2 = bidirecional\n"
                                    + "  Direção de busca. (Padrão = 1).\n"
                                    + " \n"
                                    + " -N <num>\n"
                                    + "  Número de nós que não melhoram para\n"
                                    + "  Considere antes de terminar a pesquisa.\n"
                                    + " \n"
                                    + " -S <num>\n"
                                    + "  Tamanho do cache de pesquisa para subconjuntos avaliados.\n"
                                    + "  Expresso como um múltiplo do número de Atributos no conjunto de dados. (Padrão = 1)");

                            textExpansion.setEditable(false);
                            textExpansion.setWrapText(true);
                            textExpansion.setMaxWidth(Double.MAX_VALUE);
                            textExpansion.setMaxHeight(Double.MAX_VALUE);

                            textField = new TextField();
                            textField.setMaxWidth(Double.MAX_VALUE);
                            textField.setMaxHeight(Double.MAX_VALUE);
                            GridPane.setVgrow(textField, Priority.ALWAYS);
                            GridPane.setHgrow(textField, Priority.ALWAYS);
                            GridPane.setVgrow(textExpansion, Priority.ALWAYS);
                            GridPane.setHgrow(textExpansion, Priority.ALWAYS);

                            expContent = new GridPane();
                            expContent.setHgap(10);
                            expContent.setVgap(10);
                            expContent.setMaxWidth(Double.MAX_VALUE);
                            expContent.add(textExpansion, 0, 0);
                            expContent.setPrefHeight(240);

                            mainContent = new GridPane();
                            mainContent.setHgap(10);
                            mainContent.setVgap(10);
                            mainContent.setMaxWidth(Double.MAX_VALUE);
                            mainContent.add(labelTitle, 0, 0);
                            mainContent.add(textField, 0, 1);

                            dialog.getDialogPane().setContent(mainContent);
                            dialog.getDialogPane().setExpandableContent(expContent);
                            result = dialog.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                System.out.println("OK");
                            } else if (result.get() == ButtonType.CANCEL) {
                                System.out.println("CANCEL");
                            }
                            break;

                        case "Class Conditional Probabilities":
                            dialog = new Dialog();
                            dialog.setTitle("Class Conditional Probabilities");
                            dialog.initStyle(StageStyle.UTILITY);
                            dialog.initOwner(listARFF.getScene().getWindow());
                            dialog.initModality(Modality.WINDOW_MODAL);
                            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                            labelTitle = new Label("Converte os valores dos atributos nominais e / ou numéricos em probabilidades condicionais de classe.");
                            textExpansion = new TextArea("As opções válidas são:\n"
                                    + " -N\n"
                                    + "  Não aplique essa transformação em atributos numéricos\n"
                                    + "\n"
                                    + " -C\n"
                                    + "  Não aplique essa transformação em atributos nominais\n"
                                    + "\n"
                                    + " -min-values ​​<integer>\n"
                                    + "  Transforme os atributos nominais com pelo menos esses muitos valores.\n"
                                    + "  -1 significa sempre transformar.\n"
                                    + "\n"
                                    + " -output-debug-info\n"
                                    + "  Se configurado, o filtro é executado no modo de depuração e\n"
                                    + "  Pode fornecer informações adicionais para o console\n"
                                    + "\n"
                                    + " -do-not-check-capabilities\n"
                                    + "  Se configurado, os recursos do filtro não são verificados antes do filtro ser construído\n"
                                    + "  (Use com cuidado).");

                            textExpansion.setEditable(false);
                            textExpansion.setWrapText(true);
                            textExpansion.setMaxWidth(Double.MAX_VALUE);
                            textExpansion.setMaxHeight(Double.MAX_VALUE);

                            textField = new TextField();
                            textField.setMaxWidth(Double.MAX_VALUE);
                            textField.setMaxHeight(Double.MAX_VALUE);
                            GridPane.setVgrow(textField, Priority.ALWAYS);
                            GridPane.setHgrow(textField, Priority.ALWAYS);
                            GridPane.setVgrow(textExpansion, Priority.ALWAYS);
                            GridPane.setHgrow(textExpansion, Priority.ALWAYS);

                            expContent = new GridPane();
                            expContent.setHgap(10);
                            expContent.setVgap(10);
                            expContent.setMaxWidth(Double.MAX_VALUE);
                            expContent.add(textExpansion, 0, 0);
                            expContent.setPrefHeight(240);

                            mainContent = new GridPane();
                            mainContent.setHgap(10);
                            mainContent.setVgap(10);
                            mainContent.setMaxWidth(Double.MAX_VALUE);
                            mainContent.add(labelTitle, 0, 0);
                            mainContent.add(textField, 0, 1);

                            dialog.getDialogPane().setContent(mainContent);
                            dialog.getDialogPane().setExpandableContent(expContent);
                            result = dialog.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                System.out.println("OK");
                            } else if (result.get() == ButtonType.CANCEL) {
                                System.out.println("CANCEL");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

        }
        );

        values = Arrays.asList(
                "Class Balancer",
                "Resample",
                "Spread Subsample",
                "Stratified Remove Folds"
        );

        listFilterSupInst.setItems(FXCollections.observableList(values));
        listFilterSupInst.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event
            ) {
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof ListCell) {
                    ListCell cell = (ListCell) eventTarget;
                    switch (cell.getText()) {
                        case "Abrir URL":

                            break;
                        default:
                            break;
                    }
                }
            }

        }
        );

        values = Arrays.asList(
                "Balancear Classe",
                "Resample",
                "Espelhar Subamostra",
                "Stratified Remove Folds"
        );

        listFilterNSupAttr.setItems(FXCollections.observableList(values));
        listFilterNSupAttr.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event
            ) {
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof ListCell) {
                    ListCell cell = (ListCell) eventTarget;
                    switch (cell.getText()) {
                        case "Abrir URL":
                            break;
                        default:
                            break;
                    }
                }
            }

        }
        );

        values = Arrays.asList(
                "Balancear Classe",
                "Resample",
                "Espelhar Subamostra",
                "Stratified Remove Folds"
        );

        listFilterNSupInst.setItems(FXCollections.observableList(values));
        listFilterNSupInst.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event
            ) {
                EventTarget eventTarget = event.getTarget();
                if (eventTarget instanceof ListCell) {
                    ListCell cell = (ListCell) eventTarget;
                    switch (cell.getText()) {
                        case "Abrir URL":
                            break;
                        default:
                            break;
                    }
                }
            }

        }
        );
    }
}
