package net.bhpachulski.tddcriteria.fx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.TiesStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;
import net.bhpachulski.tddcriteria.charts.DateAxis310;
import net.bhpachulski.tddcriteria.model.ExperimentalGroup;
import net.bhpachulski.tddcriteria.model.TDDCriteriaProjectProperties;
import net.bhpachulski.tddcriteria.model.analysis.TDDCriteriaProjectSnapshot;
import net.bhpachulski.tddcriteria.model.eclemma.Report;
import net.bhpachulski.tddcriteria.model.eclemma.Type;
import net.bhpachulski.tddcriteria.model.junit.TestSuiteSession;

public class FXMLController implements Initializable {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    SimpleDateFormat sdfShow = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private LineChartWithMarkers<LocalDateTime, Number> lineChart;
    private LineChartWithMarkers<LocalDateTime, Number> lineChartCoverage;

    private Map<Integer, XYChart.Data<LocalDateTime, Number>> testVerticalMarker;
    private Map<Integer, XYChart.Data<LocalDateTime, Number>> coverageVerticalMarker;

    private ChangeListener<Date> cbListenerIteracao1;
    private ChangeListener<Date> cbListenerIteracao2;
    private ChangeListener<Date> cbListenerIteracao3;
    private ChangeListener<Date> cbListenerIteracao4;
    private ChangeListener<Date> cbListenerIteracao5;
    private ChangeListener<Date> cbListenerIteracao6;

    private ChangeListener<ExperimentalGroup> cbListenerExperimentalGroup;

    DecimalFormat df = new DecimalFormat("#.00");

    private ObjectMapper xmlMapper;
    private JacksonXmlModule module;

    private TDDCriteriaProjectProperties prop;
    private File fProp;

    private StringConverter<Date> dateConverter;

    private StringConverter<ExperimentalGroup> experimentalGroupConverter;

    private static final String propFilePath = "/tddCriteria/tddCriteriaProjectProperties.xml";
    private static final String jUnitFolderPath = "/tddCriteria/junitTrack/";
    private static final String EclemmaFolderPath = "/tddCriteria/coverageTrack/";
    private static final String tddStageTrackPath = "tddStageTrack.txt";

    @FXML
    private Button btnFolder;

    @FXML
    private AnchorPane pane;

    @FXML
    private CheckBox checkCT;

    @FXML
    private CheckBox checkCTPassando;

    @FXML
    private CheckBox checkCTFalhando;

    @FXML
    private CheckBox checkInstruction;

    @FXML
    private CheckBox checkBranch;

    @FXML
    private CheckBox checkLine;

    @FXML
    private CheckBox checkComplexity;

    @FXML
    private CheckBox checkMethod;

    @FXML
    private CheckBox checkClass;

    @FXML
    private CheckBox checkTimeLineFake;
    
    @FXML
    private CheckBox checkExcluido;

    @FXML
    private ComboBox<Date> cbIteracao1;

    @FXML
    private ComboBox<Date> cbIteracao2;

    @FXML
    private ComboBox<Date> cbIteracao3;

    @FXML
    private ComboBox<Date> cbIteracao4;

    @FXML
    private ComboBox<Date> cbIteracao5;

    @FXML
    private ComboBox<Date> cbIteracao6;

    @FXML
    private ComboBox<ExperimentalGroup> cbExperimentalGroup;

    @FXML
    private Label id;

    private DirectoryChooser folderChooser = new DirectoryChooser();

    private String projectFolder = "/Users/bhpachulski/Documents/Projetos/GIT/experimentos/1415900 - Junior Nakamura/1415900";

    private XYChart.Series lnQntCasosDeTeste = new XYChart.Series();
    private XYChart.Series lnQntCasosDeTestePassando = new XYChart.Series();
    private XYChart.Series lnQntCasosDeTesteFalhando = new XYChart.Series();
    private XYChart.Series lnInstruction = new XYChart.Series();
    private XYChart.Series lnBranch = new XYChart.Series();
    private XYChart.Series lnLine = new XYChart.Series();
    private XYChart.Series lnComplexity = new XYChart.Series();
    private XYChart.Series lnMethod = new XYChart.Series();
    private XYChart.Series lnClass = new XYChart.Series();

    @FXML
    private void exibirInformacoesAction(ActionEvent event) throws IOException, ParseException {
        lineChart.setTitle("Casos de teste");
        lineChartCoverage.setTitle("Cobertura");

        limparGraficoAction();
        initChecks();
        initComboIteracao();
        lerArquivos();
    }

    @FXML
    private void buscarDiretorioAction(ActionEvent event) throws IOException {
        this.configureChooser(folderChooser);
        File file = folderChooser.showDialog(pane.getScene().getWindow());
        if (file.isDirectory()) {
            projectFolder = file.getAbsolutePath();
        } else {
            System.out.println("SHOW ERROR MESSAGE !");
        }
    }

    @FXML
    private void criarCSVAction(ActionEvent event) throws IOException, ParseException {
        this.configureChooser(folderChooser);
        File file = folderChooser.showDialog(pane.getScene().getWindow());

        StringBuilder fileContent = new StringBuilder();

        if (file.isDirectory()) {

            projectFolder = file.getAbsolutePath();

            Map<String, Map<Date, TDDCriteriaProjectSnapshot>> studentsTimeLine = readProjectByRootPath(projectFolder);

            fileContent.append("RA; HORÁRIO; TDD STAGE; QNT. CASOS DE TESTE; PASSANDO; FALHANDO; CLASS; METHOD; LINE; INSTRUCTION; BRANCH; \n");

            for (Map.Entry<String, Map<Date, TDDCriteriaProjectSnapshot>> studentTimeLineES : studentsTimeLine.entrySet()) {

                for (Map.Entry<Date, TDDCriteriaProjectSnapshot> studentTimeLine : studentTimeLineES.getValue().entrySet()) {

                    if (!studentTimeLine.getValue().getTddStage().isEmpty()) {

                        //RA
                        fileContent.append(studentTimeLineES.getKey());
                        fileContent.append("; ");

                        //HORA
                        fileContent.append(sdfShow.format(studentTimeLine.getKey()));
                        fileContent.append("; ");

                        //TDD STAGE
                        fileContent.append(studentTimeLine.getValue().getTddStage());
                        fileContent.append("; ");

                        if (studentTimeLine.getValue().getjUnitSession() != null) {

                            studentTimeLine.getValue().getjUnitSession().setTestCases(ImmutableSet.copyOf(studentTimeLine.getValue().getjUnitSession().getTestCases()).asList());

                            //JUNIT
                            //Qnt Casos de Teste 
                            fileContent.append(studentTimeLine.getValue().getjUnitSession().getTestCases().size());
                            fileContent.append("; ");

                            //Qnt Casos de Teste PASSANDO
                            fileContent.append(studentTimeLine.getValue().getjUnitSession().getTestCases().stream().filter(t -> !t.isFailed()).count());
                            fileContent.append("; ");

                            //Qnt Casos de Teste FALHANDO
                            fileContent.append(studentTimeLine.getValue().getjUnitSession().getTestCases().stream().filter(t -> t.isFailed()).count());
                            fileContent.append("; ");
                        } else {
                            fileContent.append("SJUT; SJUT; SJUT;");
                        }

                        if (studentTimeLine.getValue().getEclemmaSession() != null) {
                            studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.CLASS).collect(Collectors.toList()).stream().forEach((counter) -> {
                                fileContent.append(regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered()));
                                fileContent.append("; ");
                            });

                            studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.METHOD).collect(Collectors.toList()).stream().forEach((counter) -> {
                                fileContent.append(regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered()));
                                fileContent.append("; ");
                            });

                            studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.LINE).collect(Collectors.toList()).stream().forEach((counter) -> {
                                fileContent.append(regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered()));
                                fileContent.append("; ");
                            });

                            studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.INSTRUCTION).collect(Collectors.toList()).stream().forEach((counter) -> {
                                fileContent.append(regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered()));
                                fileContent.append("; ");
                            });

                            studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.BRANCH).collect(Collectors.toList()).stream().forEach((counter) -> {
                                fileContent.append(regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered()));
                                fileContent.append("; ");
                            });
                        } else {
                            fileContent.append("SEC; SEC; SEC; SEC; SEC;");
                        }

                        fileContent.append("\n");
                    }
                }
            }

            FileUtils.writeStringToFile(new File(projectFolder + "/studentsTimeline(" + sdf.format(new Date()) + ").csv"), fileContent.toString());
            System.out.println("DONE !");

        } else {
            System.out.println("SHOW ERROR MESSAGE !");
        }
    }

    @FXML
    private void limparGraficoAction() {
        lineChart.getData().clear();
        lineChartCoverage.getData().clear();

        lnQntCasosDeTeste = new XYChart.Series();
        lnQntCasosDeTestePassando = new XYChart.Series();
        lnQntCasosDeTesteFalhando = new XYChart.Series();
        lnInstruction = new XYChart.Series();
        lnBranch = new XYChart.Series();
        lnLine = new XYChart.Series();
//        lnComplexity = new XYChart.Series();
        lnMethod = new XYChart.Series();
        lnClass = new XYChart.Series();

        lineChart.removeAllVerticalValueMarkers();
        lineChartCoverage.removeAllVerticalValueMarkers();

        initComboIteracao();
        initChecks();
    }

    public void initChecks() {
        checkCT.setSelected(true);
        checkCTPassando.setSelected(true);
        checkCTFalhando.setSelected(true);
        checkInstruction.setSelected(true);
        checkBranch.setSelected(true);
        checkLine.setSelected(true);
        checkComplexity.setSelected(true);
        checkMethod.setSelected(true);
        checkClass.setSelected(true);

        checkComplexity.setVisible(false);
    }

    @FXML
    private void checkCTAction(ActionEvent event) {
        if (checkCT.isSelected()) {
            lineChart.getData().add(lnQntCasosDeTeste);
        } else {
            lineChart.getData().remove(lnQntCasosDeTeste);
        }
    }

    @FXML
    private void checkCTPassandoAction(ActionEvent event) {
        if (checkCTPassando.isSelected()) {
            lineChart.getData().add(lnQntCasosDeTestePassando);
        } else {
            lineChart.getData().remove(lnQntCasosDeTestePassando);
        }
    }

    @FXML
    private void checkCTFalhandoAction(ActionEvent event) {
        if (checkCTFalhando.isSelected()) {
            lineChart.getData().add(lnQntCasosDeTesteFalhando);
        } else {
            lineChart.getData().remove(lnQntCasosDeTesteFalhando);
        }
    }

    @FXML
    private void checkCoverageInstruction(ActionEvent event) {
        if (checkInstruction.isSelected()) {
            lineChartCoverage.getData().add(lnInstruction);
        } else {
            lineChartCoverage.getData().remove(lnInstruction);
        }
    }

    @FXML
    private void checkCoverageBranch(ActionEvent event) {
        if (checkBranch.isSelected()) {
            lineChartCoverage.getData().add(lnBranch);
        } else {
            lineChartCoverage.getData().remove(lnBranch);
        }
    }

    @FXML
    private void checkCoverageLine(ActionEvent event) {
        if (checkLine.isSelected()) {
            lineChartCoverage.getData().add(lnLine);
        } else {
            lineChartCoverage.getData().remove(lnLine);
        }
    }

    @FXML
    private void checkCoverageComplexity(ActionEvent event) {
        if (checkComplexity.isSelected()) {
            lineChartCoverage.getData().add(lnComplexity);
        } else {
            lineChartCoverage.getData().remove(lnComplexity);
        }
    }

    @FXML
    private void checkCoverageMethod(ActionEvent event) {
        if (checkMethod.isSelected()) {
            lineChartCoverage.getData().add(lnMethod);
        } else {
            lineChartCoverage.getData().remove(lnMethod);
        }
    }

    @FXML
    private void checkCoverageClass(ActionEvent event) {
        if (checkClass.isSelected()) {
            lineChartCoverage.getData().add(lnClass);
        } else {
            lineChartCoverage.getData().remove(lnClass);
        }
    }
    
    @FXML
    private void checkProjetoExcluido(ActionEvent event) {
        if (checkExcluido.isSelected()) {
            prop.getCurrentStudent().setExcluido(true);
        } else {
            prop.getCurrentStudent().setExcluido(false);
        }
    }

    @FXML
    private void savePropertiesFile() throws IOException {
        saveUpdatedXmlPropertiesFile();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        final StringConverter<LocalDateTime> STRING_CONVERTER = new StringConverter<LocalDateTime>() {
            @Override
            public String toString(LocalDateTime localDateTime) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy\nHH:mm:ss");
                return dtf.format(localDateTime);
            }

            @Override
            public LocalDateTime fromString(String s) {
                return LocalDateTime.parse(s);
            }
        };

        module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        xmlMapper = new XmlMapper(module);

        DateAxis310 lineChartXAxis = new DateAxis310();
        lineChartXAxis.setTickLabelFormatter(STRING_CONVERTER);

        lineChart = new LineChartWithMarkers<>(lineChartXAxis, new NumberAxis());
        lineChart.setLayoutX(0.0);
        lineChart.setLayoutY(0.0);

        lineChart.setPrefWidth(1000.0);
        lineChart.setPrefHeight(280.0);
        lineChart.autosize();

        pane.getChildren().add(lineChart);

        DateAxis310 lineChartCoverageXAxis = new DateAxis310();
        lineChartCoverageXAxis.setTickLabelFormatter(STRING_CONVERTER);
        lineChartCoverage = new LineChartWithMarkers<>(lineChartCoverageXAxis, new NumberAxis());
        lineChartCoverage.setLayoutX(5.0);
        lineChartCoverage.setLayoutY(280.0);

        lineChartCoverage.setPrefWidth(1000.0);
        lineChartCoverage.setPrefHeight(280.0);
        lineChartCoverage.autosize();

        pane.getChildren().add(lineChartCoverage);

        dateConverter = new StringConverter<Date>() {

            @Override
            public String toString(Date object) {
                return sdfShow.format(object);
            }

            @Override
            public Date fromString(String string) {
                try {
                    return sdfShow.parse(string);
                } catch (ParseException ex) {
                    return null;
                }
            }
        };

        experimentalGroupConverter = new StringConverter<ExperimentalGroup>() {
            @Override
            public String toString(ExperimentalGroup object) {
                return object.toString();
            }

            @Override
            public ExperimentalGroup fromString(String string) {
                return ExperimentalGroup.CONTROL;
            }
        };

        cbListenerIteracao1 = new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {

                changeMarker(1, newValue);
            }

        };

        cbListenerIteracao2 = new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {

                changeMarker(2, newValue);
                
            }

        };

        cbListenerIteracao3 = new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {

                changeMarker(3, newValue);
            }

        };

        cbListenerIteracao4 = new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {

                changeMarker(4, newValue);
                
            }

        };

        cbListenerIteracao5 = new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {

                changeMarker(5, newValue);
                
            }

        };

        cbListenerIteracao6 = new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {

                changeMarker(6, newValue);
                
            }

        };

        cbListenerExperimentalGroup = new ChangeListener<ExperimentalGroup>() {
            @Override
            public void changed(ObservableValue<? extends ExperimentalGroup> observable, ExperimentalGroup oldValue, ExperimentalGroup newValue) {

                System.out.println(newValue);
                prop.getCurrentStudent().setExperimentalType(newValue);

            }
        };

        initComboIteracao();

    }

    private void initComboIteracao() {

        cbIteracao1.getSelectionModel().selectedItemProperty().removeListener(cbListenerIteracao1);
        cbIteracao2.getSelectionModel().selectedItemProperty().removeListener(cbListenerIteracao2);
        cbIteracao3.getSelectionModel().selectedItemProperty().removeListener(cbListenerIteracao3);
        cbIteracao4.getSelectionModel().selectedItemProperty().removeListener(cbListenerIteracao4);
        cbIteracao5.getSelectionModel().selectedItemProperty().removeListener(cbListenerIteracao5);
        cbIteracao6.getSelectionModel().selectedItemProperty().removeListener(cbListenerIteracao6);

        cbExperimentalGroup.getSelectionModel().selectedItemProperty().removeListener(cbListenerExperimentalGroup);

        cbIteracao1.setDisable(true);
        cbIteracao2.setDisable(true);
        cbIteracao3.setDisable(true);
        cbIteracao4.setDisable(true);
        cbIteracao5.setDisable(true);
        cbIteracao6.setDisable(true);

        cbExperimentalGroup.setDisable(true);

        cbIteracao1.setConverter(dateConverter);
        cbIteracao2.setConverter(dateConverter);
        cbIteracao3.setConverter(dateConverter);
        cbIteracao4.setConverter(dateConverter);
        cbIteracao5.setConverter(dateConverter);
        cbIteracao6.setConverter(dateConverter);

        cbExperimentalGroup.setConverter(experimentalGroupConverter);

        cbIteracao1.getItems().clear();
        cbIteracao2.getItems().clear();
        cbIteracao3.getItems().clear();
        cbIteracao4.getItems().clear();
        cbIteracao5.getItems().clear();
        cbIteracao6.getItems().clear();

        cbExperimentalGroup.getItems().clear();
    }

    public void lerArquivos() throws IOException, ParseException {

        Map<Date, TDDCriteriaProjectSnapshot> projectTimeLine = readProject(projectFolder);

        lnQntCasosDeTeste.setName("Qnt. Casos de Teste");
        lnQntCasosDeTestePassando.setName("Qnt. Casos de Teste Passando");
        lnQntCasosDeTesteFalhando.setName("Qnt. Casos de Teste Falhando");

        lnInstruction.setName("Instruction Coverage");
        lnBranch.setName("Branch Coverage");
        lnLine.setName("Line Coverage");
        lnComplexity.setName("Complexity Coverage");
        lnMethod.setName("Method Coverage");
        lnClass.setName("Class Coverage");

        List<Date> iteracoes = new ArrayList<>();
        iteracoes.add(0, prop.getFirstIteration());
        iteracoes.add(1, prop.getSecondIteration());
        iteracoes.add(2, prop.getThirdIteration());
        iteracoes.add(3, prop.getFourthIteration());
        iteracoes.add(4, prop.getFifthIteration());
        iteracoes.add(5, prop.getSixthIteration());

        LocalDateTime fakeDataHoraStudentTimeline = LocalDateTime.now();

        for (Map.Entry<Date, TDDCriteriaProjectSnapshot> studentTimeLine : projectTimeLine.entrySet()) {

            if (!studentTimeLine.getValue().getTddStage().isEmpty()) {

                if ((studentTimeLine.getValue().getjUnitSession() == null) || (studentTimeLine.getValue().getEclemmaSession() == null) || (studentTimeLine.getValue().getTddStage().isEmpty())) {
                    continue;
                }

                LocalDateTime dataHoraStudentTimeline;

                if (checkTimeLineFake.isSelected()) {
                    dataHoraStudentTimeline = LocalDateTime.ofInstant(Instant.ofEpochMilli(studentTimeLine.getKey().getTime()), ZoneId.systemDefault());
                } else {
                    fakeDataHoraStudentTimeline = fakeDataHoraStudentTimeline.plusMinutes(5);
                    dataHoraStudentTimeline = fakeDataHoraStudentTimeline;

                    if (iteracoes.contains(studentTimeLine.getKey())) {
                        iteracoes.remove(studentTimeLine.getKey());
                        iteracoes.add(Date.from(fakeDataHoraStudentTimeline.atZone(ZoneId.systemDefault()).toInstant()));
                    }

                }

                //RA
                id.setText(String.valueOf(prop.getCurrentStudent().getId()));

                StringBuilder coverageTooltip = new StringBuilder();
                coverageTooltip.append(studentTimeLine.getValue().getTddStage()).append("\n");
                coverageTooltip.append("(").append(sdfShow.format(studentTimeLine.getKey())).append(")").append("\n");

                studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.CLASS).collect(Collectors.toList()).stream().forEach((counter) -> {
                    Double classCoverage = this.regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered());
                    lnClass.getData().add(new XYChart.Data(dataHoraStudentTimeline, classCoverage));
                });

                studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.METHOD).collect(Collectors.toList()).stream().forEach((counter) -> {
                    lnMethod.getData().add(new XYChart.Data(dataHoraStudentTimeline, this.regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered())));
                });

                studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.LINE).collect(Collectors.toList()).stream().forEach((counter) -> {
                    lnLine.getData().add(new XYChart.Data(dataHoraStudentTimeline, this.regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered())));
                });

                studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.INSTRUCTION).collect(Collectors.toList()).stream().forEach((counter) -> {
                    Double instructionCoverage = this.regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered());
                    lnInstruction.getData().add(new XYChart.Data(dataHoraStudentTimeline, instructionCoverage));

                    coverageTooltip.append(" * Instruction: ");
                    coverageTooltip.append(df.format(instructionCoverage));
                    coverageTooltip.append("\n");
                });

                studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.BRANCH).collect(Collectors.toList()).stream().forEach((counter) -> {
                    Double branchCoverage = this.regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered());

                    lnBranch.getData().add(new XYChart.Data(dataHoraStudentTimeline, branchCoverage));

                    coverageTooltip.append(" * Branch: ");
                    coverageTooltip.append(df.format(branchCoverage));
                    coverageTooltip.append("\n");
                });
                
                studentTimeLine.getValue().getjUnitSession().setTestCases(ImmutableSet.copyOf(studentTimeLine.getValue().getjUnitSession().getTestCases()).asList());

                //JUNIT
                //Qnt Casos de Teste                     
                XYChart.Data qntCasosTesteData = new XYChart.Data(dataHoraStudentTimeline, studentTimeLine.getValue().getjUnitSession().getTestCases().size());
                coverageTooltip.append(" * CT: ").append(studentTimeLine.getValue().getjUnitSession().getTestCases().size()).append(" | ");

                lnQntCasosDeTeste.getData().add(qntCasosTesteData);

                //Qnt Casos de Teste PASSANDO
                XYChart.Data qntCasosTestePassandoData = new XYChart.Data(dataHoraStudentTimeline, studentTimeLine.getValue().getjUnitSession().getTestCases().stream().filter(t -> !t.isFailed()).count());
                lnQntCasosDeTestePassando.getData().add(qntCasosTestePassandoData);
                coverageTooltip.append("CT P: ").append(studentTimeLine.getValue().getjUnitSession().getTestCases().stream().filter(t -> !t.isFailed()).count()).append(" | ");

                //Qnt Casos de Teste FALHANDO
                XYChart.Data qntCasosTesteFalhandoData = new XYChart.Data(dataHoraStudentTimeline, studentTimeLine.getValue().getjUnitSession().getTestCases().stream().filter(t -> t.isFailed()).count());
                lnQntCasosDeTesteFalhando.getData().add(qntCasosTesteFalhandoData);
                coverageTooltip.append("CT F: ").append(studentTimeLine.getValue().getjUnitSession().getTestCases().stream().filter(t -> t.isFailed()).count());

                int qntLinhas = studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(f -> f.getType().equals(Type.LINE)).findFirst().get().getCovered() + 
                        studentTimeLine.getValue().getEclemmaSession().getCounter().stream().filter(f -> f.getType().equals(Type.LINE)).findFirst().get().getMissed();
                
                
                coverageTooltip.append("Linhas: ").append(qntLinhas);
                
                qntCasosTesteData.setNode(new HoveredThresholdNode(coverageTooltip.toString()));
                qntCasosTestePassandoData.setNode(new HoveredThresholdNode(coverageTooltip.toString()));
                qntCasosTesteFalhandoData.setNode(new HoveredThresholdNode(coverageTooltip.toString()));
            }
        }

        lineChart.getData().clear();
        lineChart.getData().addAll(lnQntCasosDeTeste, lnQntCasosDeTestePassando, lnQntCasosDeTesteFalhando);
        lineChart.setCursor(Cursor.CROSSHAIR);

        lineChartCoverage.getData().clear();
        lineChartCoverage.getData().addAll(lnInstruction, lnBranch, lnLine, lnMethod, lnClass);
        lineChartCoverage.setCursor(Cursor.CROSSHAIR);

        testVerticalMarker = new TreeMap<>();
        coverageVerticalMarker = new TreeMap<>();

        int iterationIndex = 0;
        for (Date iteracao : iteracoes) {
            if (iteracao != null) {
                testVerticalMarker.put(iterationIndex, new XYChart.Data<>(LocalDateTime.ofInstant(Instant.ofEpochMilli(iteracao.getTime()), ZoneId.systemDefault()), 5));
                coverageVerticalMarker.put(iterationIndex, new XYChart.Data<>(LocalDateTime.ofInstant(Instant.ofEpochMilli(iteracao.getTime()), ZoneId.systemDefault()), 5));
                lineChart.addVerticalValueMarker(testVerticalMarker.get(iterationIndex));
                lineChartCoverage.addVerticalValueMarker(coverageVerticalMarker.get(iterationIndex));
            }
            iterationIndex++;
        }

        cbIteracao1.getItems().addAll(projectTimeLine.keySet());
        cbIteracao2.getItems().addAll(projectTimeLine.keySet());
        cbIteracao3.getItems().addAll(projectTimeLine.keySet());
        cbIteracao4.getItems().addAll(projectTimeLine.keySet());
        cbIteracao5.getItems().addAll(projectTimeLine.keySet());
        cbIteracao6.getItems().addAll(projectTimeLine.keySet());

        cbExperimentalGroup.getItems().add(ExperimentalGroup.INTERVENTION);
        cbExperimentalGroup.getItems().add(ExperimentalGroup.CONTROL);

        cbIteracao1.getSelectionModel().select(prop.getFirstIteration());
        cbIteracao2.getSelectionModel().select(prop.getSecondIteration());
        cbIteracao3.getSelectionModel().select(prop.getThirdIteration());
        cbIteracao4.getSelectionModel().select(prop.getFourthIteration());
        cbIteracao5.getSelectionModel().select(prop.getFifthIteration());
        cbIteracao6.getSelectionModel().select(prop.getSixthIteration());

        cbExperimentalGroup.getSelectionModel().select(prop.getCurrentStudent().getExperimentalType());

        cbIteracao1.setDisable(false);
        cbIteracao2.setDisable(false);
        cbIteracao3.setDisable(false);
        cbIteracao4.setDisable(false);
        cbIteracao5.setDisable(false);
        cbIteracao6.setDisable(false);

        cbExperimentalGroup.setDisable(false);

        cbIteracao1.setVisibleRowCount(6);
        cbIteracao2.setVisibleRowCount(6);
        cbIteracao3.setVisibleRowCount(6);
        cbIteracao4.setVisibleRowCount(6);
        cbIteracao5.setVisibleRowCount(6);
        cbIteracao6.setVisibleRowCount(6);

        cbIteracao1.getSelectionModel().selectedItemProperty().addListener(cbListenerIteracao1);
        cbIteracao2.getSelectionModel().selectedItemProperty().addListener(cbListenerIteracao2);
        cbIteracao3.getSelectionModel().selectedItemProperty().addListener(cbListenerIteracao3);
        cbIteracao4.getSelectionModel().selectedItemProperty().addListener(cbListenerIteracao4);
        cbIteracao5.getSelectionModel().selectedItemProperty().addListener(cbListenerIteracao5);
        cbIteracao6.getSelectionModel().selectedItemProperty().addListener(cbListenerIteracao6);

        cbExperimentalGroup.getSelectionModel().selectedItemProperty().addListener(cbListenerExperimentalGroup);

        MannWhitneyUTest mannTest = new MannWhitneyUTest(NaNStrategy.MINIMAL, TiesStrategy.AVERAGE);

    }

    public double regraDeTres(double total, double especificos) {
        return 100 * especificos / (double) total;
    }

    private void configureChooser(DirectoryChooser chooser) {
        chooser.setTitle("Encontre a pasta principal do projeto");
        chooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    public Map<String, Map<Date, TDDCriteriaProjectSnapshot>> readProjectByRootPath(String rootPath) throws IOException, ParseException {
        Map<String, Map<Date, TDDCriteriaProjectSnapshot>> studentsTimeline = new HashMap<>();

        for (File folder : Arrays.asList(new File(rootPath).listFiles())) {
            if (folder.isDirectory() && new File(folder.getAbsolutePath() + "/" + folder.getName().split("-")[0].trim()).exists()) {

                System.out.println(folder.getName());
                Map<Date, TDDCriteriaProjectSnapshot> timeline = readProject(folder.getAbsolutePath() + "/" + folder.getName().split("-")[0].trim());

                studentsTimeline.put(folder.getName().split("-")[0].trim(), timeline);
            }
        }

        return studentsTimeline;
    }

    public Map<Date, TDDCriteriaProjectSnapshot> readProject(String projectFolder) throws IOException, ParseException {

        module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        xmlMapper = new XmlMapper(module);

        Map<Date, TDDCriteriaProjectSnapshot> projectTimeLine = new TreeMap<>();

        fProp = new File(projectFolder + propFilePath);
        prop = xmlMapper.readValue(fProp, TDDCriteriaProjectProperties.class);
        
        checkExcluido.setSelected(prop.getCurrentStudent().isExcluido());

        List<String> tddStages = java.nio.file.Files.readAllLines(Paths.get(projectFolder + EclemmaFolderPath + tddStageTrackPath));
        for (String lnTddStages : tddStages) {

            if (!lnTddStages.trim().isEmpty()) {

                TDDCriteriaProjectSnapshot snapshotPutTDDStage = new TDDCriteriaProjectSnapshot();
                snapshotPutTDDStage.setTddStage(lnTddStages.split(":")[1]);

                projectTimeLine.put(sdf.parse(lnTddStages.substring(0, 19)), snapshotPutTDDStage);

            }
        }

        List<File> eclemmaFiles = Arrays.asList(new File(projectFolder + EclemmaFolderPath).listFiles());
        for (File eclemmaFile : eclemmaFiles) {

            if (eclemmaFile.getName().endsWith("xml")) {

                try {
                    Report rep = xmlMapper.readValue(eclemmaFile, Report.class);
                    projectTimeLine.get(sdf.parse(Files.getNameWithoutExtension(eclemmaFile.getName()).substring(0, 19))).setEclemmaSession(rep);
                } catch (Exception e) {
                    System.out.println("Eclemma File Discartado");
                }
            }

        }
        System.out.println("Eclemma files: " + eclemmaFiles.size());

        List<File> jUnitFiles = Arrays.asList(new File(projectFolder + jUnitFolderPath).listFiles());
        for (File jUnitFile : jUnitFiles) {
            if (jUnitFile.getName().endsWith("xml")) {

                try {
                    TestSuiteSession tss = xmlMapper.readValue(jUnitFile, TestSuiteSession.class);

                    projectTimeLine.get(sdf.parse(Files.getNameWithoutExtension(jUnitFile.getName()).substring(0, 19))).setjUnitSession(tss);
                } catch (Exception e) {
                    System.out.println("JUnit File Discartado");
                }
            }
        }
        System.out.println("JUnit files: " + jUnitFiles.size());

        return projectTimeLine;
    }

    private void saveUpdatedXmlPropertiesFile() throws IOException {
        if (prop != null || fProp != null) {
            xmlMapper.writeValue(fProp, prop);
        }
    }

    private void changeMarker(int iterationNumber, Date newValue) {
        
        int iterationNumberInternal = iterationNumber-1;

        if (testVerticalMarker.get(iterationNumberInternal) != null && coverageVerticalMarker.get(iterationNumberInternal) != null) {
            lineChart.removeHorizontalValueMarker(testVerticalMarker.get(iterationNumberInternal));
            lineChartCoverage.removeHorizontalValueMarker(coverageVerticalMarker.get(iterationNumberInternal));
            testVerticalMarker.remove(iterationNumberInternal);
            coverageVerticalMarker.remove(iterationNumberInternal);
        }
        
        testVerticalMarker.put(iterationNumberInternal, new XYChart.Data<>(LocalDateTime.ofInstant(Instant.ofEpochMilli(newValue.getTime()), ZoneId.systemDefault()), 5));
        coverageVerticalMarker.put(iterationNumberInternal, new XYChart.Data<>(LocalDateTime.ofInstant(Instant.ofEpochMilli(newValue.getTime()), ZoneId.systemDefault()), 5));
        lineChart.addVerticalValueMarker(testVerticalMarker.get(iterationNumberInternal));
        lineChartCoverage.addVerticalValueMarker(coverageVerticalMarker.get(iterationNumberInternal));

        setPropertiesIterationValue(iterationNumber, newValue);
    }
    
    private void setPropertiesIterationValue (int iterationNumber, Date newValue) {
        
        switch (iterationNumber) {
            case 1:
                prop.setFirstIteration(newValue);
                break;
            case 2:
                prop.setSecondIteration(newValue);
                break;
            case 3:
                prop.setThirdIteration(newValue);
                break;
            case 4:
                prop.setFourthIteration(newValue);
                break;
            case 5:
                prop.setFifthIteration(newValue);
                break;
            case 6:
                prop.setSixthIteration(newValue);
                break;
        }
        
    }

}
