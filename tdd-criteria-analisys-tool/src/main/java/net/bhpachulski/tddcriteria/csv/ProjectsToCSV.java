package net.bhpachulski.tddcriteria.csv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

import net.bhpachulski.tddcriteria.model.ExperimentalGroup;
import net.bhpachulski.tddcriteria.model.TDDCriteriaProjectProperties;
import net.bhpachulski.tddcriteria.model.analysis.TDDCriteriaProjectSnapshot;
import net.bhpachulski.tddcriteria.model.eclemma.Counter;
import net.bhpachulski.tddcriteria.model.eclemma.Report;
import net.bhpachulski.tddcriteria.model.eclemma.Type;
import net.bhpachulski.tddcriteria.model.junit.TestSuiteSession;

/**
 *
 * @author bhpachulski
 */
public class ProjectsToCSV {

    private static final String BASE_PATH = "D:/runtime-New_configuration/dsadsadsadsadsa";

	private TDDCriteriaProjectProperties prop;

    private ObjectMapper xmlMapper;
    private JacksonXmlModule module;

    private File fProp;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    SimpleDateFormat sdfShow = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private static final String propFilePath = "/tddCriteria/tddCriteriaProjectProperties.xml";
    private static final String jUnitFolderPath = "/tddCriteria/junitTrack/";
    private static final String EclemmaFolderPath = "/tddCriteria/coverageTrack/";
    private static final String tddStageTrackPath = "tddStageTrack.txt";

    public static void main(String[] args) throws IOException, ParseException {
        ProjectsToCSV p = new ProjectsToCSV();
        String retornoResumido = p.getCSVDadosResumido(new File(BASE_PATH));
        System.out.println(retornoResumido);
        System.out.println(p.getTddTimelineCSV());

        String retornoAgrupado = p.getCSVDadosAgrupados(new File(BASE_PATH));
        System.out.println(retornoAgrupado);
        System.out.println(p.getTddTimelineCSV());
    }

    public String getTddTimelineCSV() throws IOException, ParseException {

        StringBuilder fileContent = new StringBuilder();

        fileContent.append("RA; DATA; STAGE;").append("\n");

        for (Entry<String, Map<Date, String>> projectsTddStage : parseAllProjectsTddStage(BASE_PATH).entrySet()) {
            for (Entry<Date, String> projectStage : projectsTddStage.getValue().entrySet()) {
                fileContent.append(projectsTddStage.getKey()).append(";");
                fileContent.append(sdfShow.format(projectStage.getKey())).append(";");
                fileContent.append(projectStage.getValue().trim()).append(";").append("\n");
            }
        }

        return fileContent.toString();
    }

    public String getCSVDadosResumido(File file) throws IOException, ParseException {

        String projectFolder;
        StringBuilder fileContent = new StringBuilder();

        if (file.isDirectory()) {

            projectFolder = file.getAbsolutePath();

            Map<String, Map<Date, TDDCriteriaProjectSnapshot>> studentsTimeLine = parseAllProjects(projectFolder, true);

            fileContent.append("GROUP; RA; NOME; ITERACAO; ESTAGIO TDD; HORÁRIO INICIO; HORÁRIO FIM; TEMPO; QNT CASOS DE TESTE; QNT CASOS DE TESTE PASSANDO; QNT CASOS DE TESTE FALHANDO; COBERTURA DE CLASS; COBERTURA DE METODO; COBERTURA DE LINHAS; COBERTURA DE INSTRUCOES; COBERTURA DE RAMOS; \n");

            for (Map.Entry<String, Map<Date, TDDCriteriaProjectSnapshot>> studentTimeLineES : studentsTimeLine.entrySet()) {

                TDDCriteriaProjectProperties propAluno = studentTimeLineES.getValue().entrySet().stream().map(Map.Entry::getValue).findFirst().get().getCriteriaProjectProperties();

                Map<Date, TDDCriteriaProjectSnapshot> firstIteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> secondIteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> thirdteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> fourthIteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> fifthIteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> sixthIteration = new TreeMap<>();

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getFirstIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getFirstIteration()))))
                        .forEach(e -> {

                            firstIteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getFirstIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getSecondIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getSecondIteration()))))
                        .forEach(e -> {

                            secondIteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getSecondIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getThirdIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getThirdIteration()))))
                        .forEach(e -> {

                            thirdteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getThirdIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getFourthIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getFourthIteration()))))
                        .forEach(e -> {

                            fourthIteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getFourthIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getFifthIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getFifthIteration()))))
                        .forEach(e -> {

                            fifthIteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getFifthIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getSixthIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getSixthIteration()))))
                        .forEach(e -> {

                            sixthIteration.put(e.getKey(), e.getValue());

                        });

                fileContent.append(getIterationValues(firstIteration, 1, "RED", propAluno));
                fileContent.append(getIterationValues(firstIteration, 1, "GREEN", propAluno));
                fileContent.append(getIterationValues(firstIteration, 1, "REFACTOR", propAluno));

                fileContent.append(getIterationValues(secondIteration, 2, "RED", propAluno));
                fileContent.append(getIterationValues(secondIteration, 2, "GREEN", propAluno));
                fileContent.append(getIterationValues(secondIteration, 2, "REFACTOR", propAluno));

                fileContent.append(getIterationValues(thirdteration, 3, "RED", propAluno));
                fileContent.append(getIterationValues(thirdteration, 3, "GREEN", propAluno));
                fileContent.append(getIterationValues(thirdteration, 3, "REFACTOR", propAluno));

                fileContent.append(getIterationValues(fourthIteration, 4, "RED", propAluno));
                fileContent.append(getIterationValues(fourthIteration, 4, "GREEN", propAluno));
                fileContent.append(getIterationValues(fourthIteration, 4, "REFACTOR", propAluno));

                fileContent.append(getIterationValues(fifthIteration, 5, "RED", propAluno));
                fileContent.append(getIterationValues(fifthIteration, 5, "GREEN", propAluno));
                fileContent.append(getIterationValues(fifthIteration, 5, "REFACTOR", propAluno));

                fileContent.append(getIterationValues(sixthIteration, 6, "RED", propAluno));
                fileContent.append(getIterationValues(sixthIteration, 6, "GREEN", propAluno));
                fileContent.append(getIterationValues(sixthIteration, 6, "REFACTOR", propAluno));

            }

        } else {
//            System.out.println("SHOW ERROR MESSAGE !");
        }

        return fileContent.toString();

    }
    

    public String getCSVDadosAgrupados(File file) throws IOException, ParseException {

        String projectFolder;
        StringBuilder fileContent = new StringBuilder();

        if (file.isDirectory()) {

            projectFolder = file.getAbsolutePath();

            Map<String, Map<Date, TDDCriteriaProjectSnapshot>> studentsTimeLine = parseAllProjects(projectFolder, true);

            fileContent.append("ITERACAO; ESTAGIO; TIPO; GRUPO Controle; GRUPO Intervenção; P-Value; RESULTADO; \n");

            Map<Integer, Map<String, List<TDDCriteriaProjectSnapshot>>> iteracaoSnapshot = new TreeMap<>();
            iteracaoSnapshot.put(1, new TreeMap<>());
            iteracaoSnapshot.get(1).put("LAST-RED", new ArrayList<>());
            iteracaoSnapshot.get(1).put("LAST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(1).put("LAST-REFACTOR", new ArrayList<>());
            iteracaoSnapshot.get(1).put("FIRST-RED", new ArrayList<>());
            iteracaoSnapshot.get(1).put("FIRST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(1).put("FIRST-REFACTOR", new ArrayList<>());

            iteracaoSnapshot.put(2, new TreeMap<>());
            iteracaoSnapshot.get(2).put("LAST-RED", new ArrayList<>());
            iteracaoSnapshot.get(2).put("LAST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(2).put("LAST-REFACTOR", new ArrayList<>());
            iteracaoSnapshot.get(2).put("FIRST-RED", new ArrayList<>());
            iteracaoSnapshot.get(2).put("FIRST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(2).put("FIRST-REFACTOR", new ArrayList<>());

            iteracaoSnapshot.put(3, new TreeMap<>());
            iteracaoSnapshot.get(3).put("LAST-RED", new ArrayList<>());
            iteracaoSnapshot.get(3).put("LAST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(3).put("LAST-REFACTOR", new ArrayList<>());
            iteracaoSnapshot.get(3).put("FIRST-RED", new ArrayList<>());
            iteracaoSnapshot.get(3).put("FIRST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(3).put("FIRST-REFACTOR", new ArrayList<>());

            iteracaoSnapshot.put(4, new TreeMap<>());
            iteracaoSnapshot.get(4).put("LAST-RED", new ArrayList<>());
            iteracaoSnapshot.get(4).put("LAST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(4).put("LAST-REFACTOR", new ArrayList<>());
            iteracaoSnapshot.get(4).put("FIRST-RED", new ArrayList<>());
            iteracaoSnapshot.get(4).put("FIRST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(4).put("FIRST-REFACTOR", new ArrayList<>());

            iteracaoSnapshot.put(5, new TreeMap<>());
            iteracaoSnapshot.get(5).put("LAST-RED", new ArrayList<>());
            iteracaoSnapshot.get(5).put("LAST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(5).put("LAST-REFACTOR", new ArrayList<>());
            iteracaoSnapshot.get(5).put("FIRST-RED", new ArrayList<>());
            iteracaoSnapshot.get(5).put("FIRST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(5).put("FIRST-REFACTOR", new ArrayList<>());

            iteracaoSnapshot.put(6, new TreeMap<>());
            iteracaoSnapshot.get(6).put("LAST-RED", new ArrayList<>());
            iteracaoSnapshot.get(6).put("LAST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(6).put("LAST-REFACTOR", new ArrayList<>());
            iteracaoSnapshot.get(6).put("FIRST-RED", new ArrayList<>());
            iteracaoSnapshot.get(6).put("FIRST-GREEN", new ArrayList<>());
            iteracaoSnapshot.get(6).put("FIRST-REFACTOR", new ArrayList<>());

            for (Map.Entry<String, Map<Date, TDDCriteriaProjectSnapshot>> studentTimeLineES : studentsTimeLine.entrySet()) {

                TDDCriteriaProjectProperties propAluno = studentTimeLineES.getValue().entrySet().stream().map(Map.Entry::getValue).findFirst().get().getCriteriaProjectProperties();

                Map<Date, TDDCriteriaProjectSnapshot> firstIteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> secondIteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> thirdteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> fourthIteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> fifithIteration = new TreeMap<>();
                Map<Date, TDDCriteriaProjectSnapshot> sixthIteration = new TreeMap<>();

                System.out.println(" ");
                System.out.println("******* " + propAluno.getCurrentStudent().getName());
                System.out.println(" ");

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getFirstIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getFirstIteration()))))
                        .forEach(e -> {

                            firstIteration.put(e.getKey(), e.getValue());

                        });
                
                firstIteration.entrySet().stream().forEach(o -> {
                    System.out.println(o.getValue().getTddStage());
                });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getFirstIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getSecondIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getSecondIteration()))))
                        .forEach(e -> {

                            secondIteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getSecondIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getThirdIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getThirdIteration()))))
                        .forEach(e -> {

                            thirdteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getThirdIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getFourthIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getFourthIteration()))))
                        .forEach(e -> {

                            fourthIteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getFourthIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getFifthIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getFifthIteration()))))
                        .forEach(e -> {

                            fifithIteration.put(e.getKey(), e.getValue());

                        });

                studentTimeLineES.getValue().entrySet().stream()
                        .filter(e
                                -> (new DateTime(e.getKey()).isAfter(new DateTime(propAluno.getFifthIteration())))
                                && (new DateTime(e.getKey()).isBefore(new DateTime(propAluno.getSixthIteration())))
                                || (new DateTime(e.getKey()).isEqual(new DateTime(propAluno.getSixthIteration()))))
                        .forEach(e -> {

                            sixthIteration.put(e.getKey(), e.getValue());

                        });

                System.out.println("Iteração 01: ");

                TDDCriteriaProjectSnapshot lastRedFirstIteration = getLastIterationStageSnapshot(firstIteration, "RED");
                System.out.println("Last RED: " + lastRedFirstIteration);
                iteracaoSnapshot.get(1).get("LAST-RED").add(lastRedFirstIteration);

                TDDCriteriaProjectSnapshot lastGreenFirstIteration = getLastIterationStageSnapshot(firstIteration, "GREEN");
                System.out.println("Last GREEN: " + lastRedFirstIteration);
                iteracaoSnapshot.get(1).get("LAST-GREEN").add(lastGreenFirstIteration);

                TDDCriteriaProjectSnapshot lastRefactorFirstIteration = getLastIterationStageSnapshot(firstIteration, "REFACTOR");
                System.out.println("Last REFACTOR: " + lastRedFirstIteration);
                iteracaoSnapshot.get(1).get("LAST-REFACTOR").add(lastRefactorFirstIteration);

                System.out.println(" ");

                TDDCriteriaProjectSnapshot firstRedFirstIteration = getFirstIterationStageSnapshot(firstIteration, "RED");
                System.out.println("First RED: " + firstRedFirstIteration);
                iteracaoSnapshot.get(1).get("FIRST-RED").add(firstRedFirstIteration);

                TDDCriteriaProjectSnapshot firstGreenFirstIteration = getFirstIterationStageSnapshot(firstIteration, "GREEN");
                System.out.println("First GREEN: " + firstGreenFirstIteration);
                iteracaoSnapshot.get(1).get("FIRST-GREEN").add(firstGreenFirstIteration);

                TDDCriteriaProjectSnapshot firstRefactorFirstIteration = getFirstIterationStageSnapshot(firstIteration, "REFACTOR");
                System.out.println("First REFACTOR: " + firstRefactorFirstIteration);
                iteracaoSnapshot.get(1).get("FIRST-REFACTOR").add(firstRefactorFirstIteration);

                System.out.println(" ");

                System.out.println("Iteração 02: ");

                TDDCriteriaProjectSnapshot lastRedSecondIteration = getLastIterationStageSnapshot(secondIteration, "RED");
                System.out.println("Last RED: " + lastRedSecondIteration);
                iteracaoSnapshot.get(2).get("LAST-RED").add(lastRedSecondIteration);

                TDDCriteriaProjectSnapshot lastGreenSecondIteration = getLastIterationStageSnapshot(secondIteration, "GREEN");
                System.out.println("Last GREEN: " + lastGreenSecondIteration);
                iteracaoSnapshot.get(2).get("LAST-GREEN").add(lastGreenSecondIteration);

                TDDCriteriaProjectSnapshot lastRefactorSecondIteration = getLastIterationStageSnapshot(secondIteration, "REFACTOR");
                System.out.println("Last REFACTOR: " + lastRefactorSecondIteration);
                iteracaoSnapshot.get(2).get("LAST-REFACTOR").add(lastRefactorSecondIteration);

                System.out.println(" ");

                TDDCriteriaProjectSnapshot firstRedSecondIteration = getFirstIterationStageSnapshot(secondIteration, "RED");
                System.out.println("First RED: " + firstRedSecondIteration);
                iteracaoSnapshot.get(2).get("FIRST-RED").add(firstRedSecondIteration);

                TDDCriteriaProjectSnapshot firstGreenSecondIteration = getFirstIterationStageSnapshot(secondIteration, "GREEN");
                System.out.println("First GREEN: " + firstGreenSecondIteration);
                iteracaoSnapshot.get(2).get("FIRST-GREEN").add(firstGreenSecondIteration);

                TDDCriteriaProjectSnapshot firstRefactorSecondIteration = getFirstIterationStageSnapshot(secondIteration, "REFACTOR");
                System.out.println("First REFACTOR: " + firstRefactorSecondIteration);
                iteracaoSnapshot.get(2).get("FIRST-REFACTOR").add(firstRefactorSecondIteration);

                System.out.println(" ");

                System.out.println("Iteração 03: ");

                TDDCriteriaProjectSnapshot lastRedThirdIteration = getLastIterationStageSnapshot(thirdteration, "RED");
                System.out.println("Last RED: " + lastRedThirdIteration);
                iteracaoSnapshot.get(3).get("LAST-RED").add(lastRedThirdIteration);

                TDDCriteriaProjectSnapshot lastGreenThirdIteration = getLastIterationStageSnapshot(thirdteration, "GREEN");
                System.out.println("Last GREEN: " + lastGreenThirdIteration);
                iteracaoSnapshot.get(3).get("LAST-GREEN").add(lastGreenThirdIteration);

                TDDCriteriaProjectSnapshot lastRefactorThirdIteration = getLastIterationStageSnapshot(thirdteration, "REFACTOR");
                System.out.println("Last REFACTOR: " + lastRefactorThirdIteration);
                iteracaoSnapshot.get(3).get("LAST-REFACTOR").add(lastRefactorThirdIteration);

                System.out.println(" ");

                TDDCriteriaProjectSnapshot firstRedThirdIteration = getFirstIterationStageSnapshot(thirdteration, "RED");
                System.out.println("First RED: " + firstRedThirdIteration);
                iteracaoSnapshot.get(3).get("FIRST-RED").add(firstRedThirdIteration);

                TDDCriteriaProjectSnapshot firstGreenThirdIteration = getFirstIterationStageSnapshot(thirdteration, "GREEN");
                System.out.println("First GREEN: " + firstGreenThirdIteration);
                iteracaoSnapshot.get(3).get("FIRST-GREEN").add(firstGreenThirdIteration);

                TDDCriteriaProjectSnapshot firstRefactorThirdIteration = getFirstIterationStageSnapshot(thirdteration, "REFACTOR");
                System.out.println("First REFACTOR: " + firstRefactorThirdIteration);
                iteracaoSnapshot.get(3).get("FIRST-REFACTOR").add(firstRefactorThirdIteration);

                System.out.println(" ");

                System.out.println("Iteração 04: ");

                TDDCriteriaProjectSnapshot lastRedFourthIteration = getLastIterationStageSnapshot(fourthIteration, "RED");
                System.out.println("Last RED: " + lastRedFourthIteration);
                iteracaoSnapshot.get(4).get("LAST-RED").add(lastRedFourthIteration);

                TDDCriteriaProjectSnapshot lastGreenFourthIteration = getLastIterationStageSnapshot(fourthIteration, "GREEN");
                System.out.println("Last GREEN: " + lastGreenFourthIteration);
                iteracaoSnapshot.get(4).get("LAST-GREEN").add(lastGreenFourthIteration);

                TDDCriteriaProjectSnapshot lastRefactorFourthIteration = getLastIterationStageSnapshot(fourthIteration, "REFACTOR");
                System.out.println("Last REFACTOR: " + lastRefactorFourthIteration);
                iteracaoSnapshot.get(4).get("LAST-REFACTOR").add(lastRefactorFourthIteration);

                System.out.println(" ");

                TDDCriteriaProjectSnapshot firstRedFourthIteration = getFirstIterationStageSnapshot(fourthIteration, "RED");
                System.out.println("First RED: " + firstRedFourthIteration);
                iteracaoSnapshot.get(4).get("FIRST-RED").add(firstRedFourthIteration);

                TDDCriteriaProjectSnapshot firstGreenFourthIteration = getFirstIterationStageSnapshot(fourthIteration, "GREEN");
                System.out.println("First GREEN: " + firstGreenFourthIteration);
                iteracaoSnapshot.get(4).get("FIRST-GREEN").add(firstGreenFourthIteration);

                TDDCriteriaProjectSnapshot firstRefactorFourthIteration = getFirstIterationStageSnapshot(fourthIteration, "REFACTOR");
                System.out.println("First REFACTOR: " + firstRefactorFourthIteration);
                iteracaoSnapshot.get(4).get("FIRST-REFACTOR").add(firstRefactorFourthIteration);

                System.out.println(" ");

                System.out.println("Iteração 05: ");

                TDDCriteriaProjectSnapshot lastRedFifthIteration = getLastIterationStageSnapshot(fifithIteration, "RED");
                System.out.println("Last RED: " + lastRedFifthIteration);
                iteracaoSnapshot.get(5).get("LAST-RED").add(lastRedFifthIteration);

                TDDCriteriaProjectSnapshot lastGreenFifthIteration = getLastIterationStageSnapshot(fifithIteration, "GREEN");
                System.out.println("Last GREEN: " + lastGreenFifthIteration);
                iteracaoSnapshot.get(5).get("LAST-GREEN").add(lastGreenFifthIteration);

                TDDCriteriaProjectSnapshot lastRefactorFifthIteration = getLastIterationStageSnapshot(fifithIteration, "REFACTOR");
                System.out.println("Last REFACTOR: " + lastRefactorFifthIteration);
                iteracaoSnapshot.get(5).get("LAST-REFACTOR").add(lastRefactorFifthIteration);

                System.out.println(" ");

                TDDCriteriaProjectSnapshot firstRedFifthIteration = getFirstIterationStageSnapshot(fifithIteration, "RED");
                System.out.println("First RED: " + firstRedFifthIteration);
                iteracaoSnapshot.get(5).get("FIRST-RED").add(firstRedFifthIteration);

                TDDCriteriaProjectSnapshot firstGreenFifthIteration = getFirstIterationStageSnapshot(fifithIteration, "GREEN");
                System.out.println("First GREEN: " + firstGreenFifthIteration);
                iteracaoSnapshot.get(5).get("FIRST-GREEN").add(firstGreenFifthIteration);

                TDDCriteriaProjectSnapshot firstRefactorFifthIteration = getFirstIterationStageSnapshot(fifithIteration, "REFACTOR");
                System.out.println("First REFACTOR: " + firstRefactorFifthIteration);
                iteracaoSnapshot.get(5).get("FIRST-REFACTOR").add(firstRefactorFifthIteration);

                System.out.println(" ");

                System.out.println("Iteração 06: ");

                TDDCriteriaProjectSnapshot lastRedSixthIteration = getLastIterationStageSnapshot(sixthIteration, "RED");
                System.out.println("Last RED: " + lastRedSixthIteration);
                iteracaoSnapshot.get(6).get("LAST-RED").add(lastRedSixthIteration);

                TDDCriteriaProjectSnapshot lastGreenSixthIteration = getLastIterationStageSnapshot(sixthIteration, "GREEN");
                System.out.println("Last GREEN: " + lastGreenSixthIteration);
                iteracaoSnapshot.get(6).get("LAST-GREEN").add(lastGreenSixthIteration);

                TDDCriteriaProjectSnapshot lastRefactorSixthIteration = getLastIterationStageSnapshot(sixthIteration, "REFACTOR");
                System.out.println("Last REFACTOR: " + lastRefactorSixthIteration);
                iteracaoSnapshot.get(6).get("LAST-REFACTOR").add(lastRefactorSixthIteration);

                System.out.println(" ");

                TDDCriteriaProjectSnapshot firstRedSixthIteration = getFirstIterationStageSnapshot(sixthIteration, "RED");
                System.out.println("First RED: " + firstRedSixthIteration);
                iteracaoSnapshot.get(6).get("FIRST-RED").add(firstRedSixthIteration);

                TDDCriteriaProjectSnapshot firstGreenSixthIteration = getFirstIterationStageSnapshot(sixthIteration, "GREEN");
                System.out.println("First GREEN: " + firstGreenSixthIteration);
                iteracaoSnapshot.get(6).get("FIRST-GREEN").add(firstGreenSixthIteration);

                TDDCriteriaProjectSnapshot firstRefactorSixthIteration = getFirstIterationStageSnapshot(sixthIteration, "REFACTOR");
                System.out.println("First REFACTOR: " + firstRefactorSixthIteration);
                iteracaoSnapshot.get(6).get("FIRST-REFACTOR").add(firstRefactorSixthIteration);

            }

            System.out.println("");
            System.out.println(" ********** Avaliação dos Grupos ********** ");
            System.out.println("");

            MannWhitneyUTest mannTest = new MannWhitneyUTest(NaNStrategy.MINIMAL, TiesStrategy.MINIMUM);

            for (int i = 1; i <= 6; i++) {

                System.out.println("Iteração: " + i);

                for (String estagio : Arrays.asList(new String[]{"RED", "GREEN", "REFACTOR"})) {

                    System.out.println("   - Estágio do TDD: " + estagio);
                    
                    List<TDDCriteriaProjectSnapshot> controleLast = iteracaoSnapshot.get(i).get("LAST-" + estagio).stream()
                            .filter(f -> f != null)
                            .filter(f -> !f.getCriteriaProjectProperties().getCurrentStudent().isExcluido())
                            .filter(f -> f.getCriteriaProjectProperties().getCurrentStudent().getExperimentalType().equals(ExperimentalGroup.CONTROL))
                            .collect(Collectors.toList());

                    List<TDDCriteriaProjectSnapshot> controleFirst = iteracaoSnapshot.get(i).get("FIRST-" + estagio).stream()
                            .filter(f -> f != null)
                            .filter(f -> !f.getCriteriaProjectProperties().getCurrentStudent().isExcluido())
                            .filter(f -> f.getCriteriaProjectProperties().getCurrentStudent().getExperimentalType().equals(ExperimentalGroup.CONTROL))
                            .collect(Collectors.toList());

                    List<TDDCriteriaProjectSnapshot> interventionLast = iteracaoSnapshot.get(i).get("LAST-" + estagio).stream()
                            .filter(f -> f != null)
                            .filter(f -> !f.getCriteriaProjectProperties().getCurrentStudent().isExcluido())
                            .filter(f -> f.getCriteriaProjectProperties().getCurrentStudent().getExperimentalType().equals(ExperimentalGroup.INTERVENTION))
                            .collect(Collectors.toList());

                    List<TDDCriteriaProjectSnapshot> interventionFirst = iteracaoSnapshot.get(i).get("FIRST-" + estagio).stream()
                            .filter(f -> f != null)
                            .filter(f -> !f.getCriteriaProjectProperties().getCurrentStudent().isExcluido())
                            .filter(f -> f.getCriteriaProjectProperties().getCurrentStudent().getExperimentalType().equals(ExperimentalGroup.INTERVENTION))
                            .collect(Collectors.toList());

                    for (Type type : Arrays.asList(new Type[]{Type.INSTRUCTION, Type.BRANCH})) {

                        List<Double> controlGroupValue = new ArrayList<>();
                        List<Double> interventionGroupValue = new ArrayList<>();

                        for (TDDCriteriaProjectSnapshot snapControle : controleLast) {
                            double val = coverageCounter(snapControle, type);

                            if (!Double.isNaN(val)) {
                                controlGroupValue.add(val);
                            }
                        }

                        for (TDDCriteriaProjectSnapshot snapintervention : interventionLast) {
                            double val = coverageCounter(snapintervention, type);

                            if (!Double.isNaN(val)) {
                                interventionGroupValue.add(val);
                            }
                        }

                        System.out.println("     + " + type);
                        System.out.println("       . Controle: " + controlGroupValue);
                        System.out.println("       . Intervenção: " + interventionGroupValue);
                        
                        fileContent.append(i).append("; ");
                        fileContent.append(estagio).append("; ");
                        
                        fileContent.append(type).append("; ");
                        
                        fileContent.append(controlGroupValue).append("; ");
                        fileContent.append(interventionGroupValue).append("; ");

                        if (!controlGroupValue.isEmpty() && !interventionGroupValue.isEmpty()) {

                            double mannResult = mannTest.mannWhitneyUTest(controlGroupValue.stream().mapToDouble(Double::doubleValue).toArray(),
                                    interventionGroupValue.stream().mapToDouble(Double::doubleValue).toArray());

                            System.out.println("       . MannWhitneyUTest: "
                                    + ((mannResult < 0.05) ? "DIFERENÇA SIGNIFICATIVA" : "DIFERENÇA NÃO É SIGNIFICATIVA") + " (" + mannResult + ") "
                            );
                            
                            fileContent.append(mannResult).append("; ");
                            
                            fileContent.append(((mannResult < 0.05) ? "DIFERENÇA SIGNIFICATIVA" : "DIFERENÇA NÃO É SIGNIFICATIVA")).append("; ");
                            fileContent.append("\n");
                        } else {
                            fileContent.append(" ; ").append(" ; ").append("\n");
                        }

                    }

                    ArrayList<Double> controleDiferencaMinutos = extractIteractionDuration(controleFirst, controleLast);
                    ArrayList<Double> intervencaoDiferencaMinutos = extractIteractionDuration(interventionFirst, interventionLast);
                    
                    fileContent.append(i).append("; ");
                    fileContent.append(estagio).append("; ");
                    
                    fileContent.append("TEMPO").append("; ");
                    
                    fileContent.append(controleDiferencaMinutos).append("; ");
                    fileContent.append(intervencaoDiferencaMinutos).append("; ");
                    

                    System.out.println("     + Tempo das Iterações");
                    System.out.println("       . Controle: " + controleDiferencaMinutos);
                    System.out.println("       . Intervenção: " + intervencaoDiferencaMinutos);

                    if (!controleDiferencaMinutos.isEmpty() && !intervencaoDiferencaMinutos.isEmpty()) {

                        double mannResult = mannTest.mannWhitneyUTest(controleDiferencaMinutos.stream().mapToDouble(Double::doubleValue).toArray(),
                                intervencaoDiferencaMinutos.stream().mapToDouble(Double::doubleValue).toArray());

                        System.out.println("       . MannWhitneyUTest: "
                                + ((mannResult < 0.05) ? "DIFERENÇA SIGNIFICATIVA" : "DIFERENÇA NÃO É SIGNIFICATIVA") + " (" + mannResult + ") "
                        );
                        
                        fileContent.append(mannResult).append("; ");
                        fileContent.append(((mannResult < 0.05) ? "DIFERENÇA SIGNIFICATIVA" : "DIFERENÇA NÃO É SIGNIFICATIVA")).append("; ");
                        fileContent.append("\n");
                    } else {
                        fileContent.append(" ; ").append(" ; ").append("\n");
                    }
                    
                }
            }

        } else {
//            System.out.println("SHOW ERROR MESSAGE !");
        }        

        return fileContent.toString();

    }

    public ArrayList<Double> extractIteractionDuration(List<TDDCriteriaProjectSnapshot> groupFirst, List<TDDCriteriaProjectSnapshot> groupLast) {

        ArrayList<Double> diferencaMinutos = new ArrayList<>();

        for (TDDCriteriaProjectSnapshot snapControleFirst : groupFirst) {

            Date dtFirst = null;
            Date dtLast = null;

            if (snapControleFirst.getEclemmaSession() != null) {
                dtFirst = snapControleFirst.getEclemmaSession().getSessioninfo().getStart();
            } else if (snapControleFirst.getjUnitSession() != null) {
                dtFirst = snapControleFirst.getjUnitSession().getLaunched();
            }

            TDDCriteriaProjectSnapshot snapControleLast = groupLast.stream()
                    .filter(f -> f.getCriteriaProjectProperties().getCurrentStudent().getId() == snapControleFirst.getCriteriaProjectProperties().getCurrentStudent().getId())
                    .findFirst().get();

            if (snapControleLast.getEclemmaSession() != null) {
                dtLast = snapControleLast.getEclemmaSession().getSessioninfo().getStart();
            } else if (snapControleLast.getjUnitSession() != null) {
                dtLast = snapControleLast.getjUnitSession().getLaunched();
            }

            if (dtFirst != null && dtLast != null) {
                Interval diferencaHoras = new Interval(new DateTime(dtFirst), new DateTime(dtLast));
                diferencaMinutos.add((double) diferencaHoras.toPeriod().getMinutes());
            }
        }

        return diferencaMinutos;
    }

    public String getCSVDadosCompletos(File file) throws IOException, ParseException {

        String projectFolder;
        StringBuilder fileContent = new StringBuilder();

        if (file.isDirectory()) {

            projectFolder = file.getAbsolutePath();

            Map<String, Map<Date, TDDCriteriaProjectSnapshot>> studentsTimeLine = parseAllProjects(projectFolder, true);

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

        } else {
            System.out.println("SHOW ERROR MESSAGE !");
        }

        return fileContent.toString();
    }

    public Map<String, Map<Date, TDDCriteriaProjectSnapshot>> parseAllProjects(String rootPath, boolean ignoreExcluded) throws IOException, ParseException {

        Map<String, Map<Date, TDDCriteriaProjectSnapshot>> studentsTimeline = new TreeMap<>();

        for (File folder : getProjectRootFolders(rootPath)) {
            System.out.println(" ----------------------------------- ");
            System.out.println(" * " + folder.getName());
            Map<Date, TDDCriteriaProjectSnapshot> timeline = readProject(folder.getAbsolutePath() + "/" + folder.getName().split("-")[0].trim());

            if (ignoreExcluded && !timeline.entrySet().stream().findFirst().get().getValue().getCriteriaProjectProperties().getCurrentStudent().isExcluido()) {
                studentsTimeline.put(folder.getName().split("-")[0].trim(), timeline);
            }
        }

        return studentsTimeline;
    }

    public Map<String, Map<Date, String>> parseAllProjectsTddStage(String rootPath) throws IOException, ParseException {

        Map<String, Map<Date, String>> tddStageStudentTimeline = new TreeMap<>();

        for (File folder : getProjectRootFolders(rootPath)) {
            tddStageStudentTimeline.put(folder.getName().split("-")[0].trim(), getTDDTimeline(folder.getAbsolutePath() + "/" + folder.getName().split("-")[0].trim()));
        }

        return tddStageStudentTimeline;
    }

    public List<File> getProjectRootFolders(String rootPath) throws IOException, ParseException {

        List<File> folders = new ArrayList<>();

        for (File folder : Arrays.asList(new File(rootPath).listFiles())) {
            if (folder.isDirectory() && new File(folder.getAbsolutePath() + "/" + folder.getName().split("-")[0].trim()).exists()) {

                folders.add(folder);

            }
        }

        return folders;
    }

    public Map<Date, TDDCriteriaProjectSnapshot> readProject(String projectFolder) throws IOException, ParseException {

        module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        xmlMapper = new XmlMapper(module);

        Map<Date, TDDCriteriaProjectSnapshot> projectTimeLine = new TreeMap<>();

        fProp = new File(projectFolder + propFilePath);
        prop = xmlMapper.readValue(fProp, TDDCriteriaProjectProperties.class);

        System.out.println("   + " + "Grupo: " + prop.getCurrentStudent().getExperimentalType());
        System.out.println("   + " + "Excluído?: " + prop.getCurrentStudent().isExcluido());

        List<String> tddStages = java.nio.file.Files.readAllLines(Paths.get(projectFolder + EclemmaFolderPath + tddStageTrackPath));
        for (String lnTddStages : tddStages) {

            if (!lnTddStages.trim().isEmpty()) {

                TDDCriteriaProjectSnapshot snapshotPutTDDStage = new TDDCriteriaProjectSnapshot();
                snapshotPutTDDStage.setTddStage(lnTddStages.split(":")[1]);

                projectTimeLine.put(sdf.parse(lnTddStages.substring(0, 19)), snapshotPutTDDStage);

                projectTimeLine.get(sdf.parse(lnTddStages.substring(0, 19))).setCriteriaProjectProperties(prop);

            }
        }

        List<File> eclemmaFiles = Arrays.asList(new File(projectFolder + EclemmaFolderPath).listFiles());
        for (File eclemmaFile : eclemmaFiles) {

            if (eclemmaFile.getName().endsWith("xml")) {

                try {
                    Report rep = xmlMapper.readValue(eclemmaFile, Report.class);
                    projectTimeLine.get(sdf.parse(Files.getNameWithoutExtension(eclemmaFile.getName()).substring(0, 19))).setEclemmaSession(rep);
                } catch (Exception e) {
//                    System.out.println("Eclemma File Discartado");
                }
            }

        }
        System.out.println("   + " + "Eclemma files: " + eclemmaFiles.size());

        List<File> jUnitFiles = Arrays.asList(new File(projectFolder + jUnitFolderPath).listFiles());
        for (File jUnitFile : jUnitFiles) {
            if (jUnitFile.getName().endsWith("xml")) {

                try {
                    TestSuiteSession tss = xmlMapper.readValue(jUnitFile, TestSuiteSession.class);
                    projectTimeLine.get(sdf.parse(Files.getNameWithoutExtension(jUnitFile.getName()).substring(0, 19))).setjUnitSession(tss);
                } catch (Exception e) {
//                    System.out.println("JUnit File Discartado");
                }
            }
        }
        System.out.println("   + " + "JUnit files: " + jUnitFiles.size());

        return projectTimeLine;
    }

    public static double regraDeTres(double total, double especificos) {
        return 100 * especificos / (double) total;
    }

    public String getIterationValues(Map<Date, TDDCriteriaProjectSnapshot> iteration, Integer iteracao, String tddStage, TDDCriteriaProjectProperties propAluno) {

        StringBuilder studentLine = new StringBuilder();

        studentLine.append(propAluno.getCurrentStudent().getExperimentalType()).append(";");
        studentLine.append(propAluno.getCurrentStudent().getId()).append("; ").append(propAluno.getCurrentStudent().getName()).append(";");
        studentLine.append(iteracao).append(";");
        studentLine.append(tddStage).append(";");

        if (iteration.entrySet().stream().filter(e -> e.getValue().getTddStage().trim().equals(tddStage)).count() > 0) {

            Entry<Date, TDDCriteriaProjectSnapshot> first = iteration.entrySet().stream().filter(e -> e.getValue().getTddStage().trim().equals(tddStage)).findFirst().get();
            Entry<Date, TDDCriteriaProjectSnapshot> last = iteration.entrySet().stream().filter(e -> e.getValue().getTddStage().trim().equals(tddStage)).reduce((a, b) -> b).get();

            studentLine.append(sdfShow.format(first.getKey())).append(";");
            studentLine.append(sdfShow.format(last.getKey())).append(";");

            Interval diferencaHoras = new Interval(new DateTime(first.getKey()), new DateTime(last.getKey()));
            studentLine.append(diferencaHoras.toPeriod().getMinutes());
            studentLine.append(";");

            if (last.getValue().getjUnitSession() != null) {
                //Qnt Casos de Teste 
                studentLine.append(last.getValue().getjUnitSession().getTestCases().size());
                studentLine.append(";");

                //Qnt Casos de Teste PASSANDO
                studentLine.append(last.getValue().getjUnitSession().getTestCases().stream().filter(t -> !t.isFailed()).count());
                studentLine.append(";");

                studentLine.append(last.getValue().getjUnitSession().getTestCases().stream().filter(t -> t.isFailed()).count());
                studentLine.append(";");

            } else {
                studentLine.append("s/r;s/r;s/r;");
            }

            if (last.getValue().getEclemmaSession() != null) {
                last.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.CLASS).collect(Collectors.toList()).stream().forEach((counter) -> {
                    double classCoverage = regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered());
                    studentLine.append(classCoverage);
                    studentLine.append(";");
                });

                last.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.METHOD).collect(Collectors.toList()).stream().forEach((counter) -> {
                    studentLine.append(regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered()));
                    studentLine.append(";");
                });

                last.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.LINE).collect(Collectors.toList()).stream().forEach((counter) -> {
                    studentLine.append(regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered()));
                    studentLine.append(";");
                });

                last.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.INSTRUCTION).collect(Collectors.toList()).stream().forEach((counter) -> {
                    studentLine.append(regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered()));
                    studentLine.append(";");
                });

                last.getValue().getEclemmaSession().getCounter().stream().filter(t -> t.getType() == Type.BRANCH).collect(Collectors.toList()).stream().forEach((counter) -> {
                    double branchCoverage = regraDeTres(counter.getMissed() + counter.getCovered(), counter.getCovered());
                    studentLine.append(branchCoverage);
                    studentLine.append(";");
                });

            } else {
                studentLine.append("s/r;s/r;s/r;s/r;s/r;");
            }

        } else {
            studentLine.append("s/r;s/r;s/r;s/r;s/r;s/r;s/r;s/r;s/r;s/r;s/r;");
        }

        studentLine.append(" \n");

        return studentLine.toString();
    }

    public TDDCriteriaProjectSnapshot getLastIterationStageSnapshot(Map<Date, TDDCriteriaProjectSnapshot> iteration, String tddStage) {

        if (iteration.entrySet().stream().filter(e -> e.getValue().getTddStage().trim().equals(tddStage)).count() > 0) {
            return iteration.entrySet().stream().filter(e -> e.getValue().getTddStage().trim().equals(tddStage)).reduce((a, b) -> b).get().getValue();
        }

        return null;
    }

    public TDDCriteriaProjectSnapshot getFirstIterationStageSnapshot(Map<Date, TDDCriteriaProjectSnapshot> iteration, String tddStage) {

        if (iteration.entrySet().stream().filter(e -> e.getValue().getTddStage().trim().equals(tddStage)).count() > 0) {
            return iteration.entrySet().stream().filter(e -> e.getValue().getTddStage().trim().equals(tddStage)).findFirst().get().getValue();
        }

        return null;
    }

    public Map<Date, String> getTDDTimeline(String projectFolder) throws IOException, ParseException {

        Map<Date, String> tddStageTimeline = new TreeMap<>();

        List<String> tddStages = java.nio.file.Files.readAllLines(Paths.get(projectFolder + EclemmaFolderPath + tddStageTrackPath));
        for (String lnTddStages : tddStages) {

            if (!lnTddStages.trim().isEmpty()) {

                tddStageTimeline.put(sdf.parse(lnTddStages.substring(0, 19)), lnTddStages.split(":")[1]);

            }
        }

        return tddStageTimeline;
    }

    public double coverageCounter(TDDCriteriaProjectSnapshot snapshot, Type eclemmaType) {
        try {
            Counter counterControle = snapshot.getEclemmaSession().getCounter().stream()
                    .filter(f -> f.getType().equals(eclemmaType))
                    .findFirst().get();

            return regraDeTres(counterControle.getMissed() + counterControle.getCovered(), counterControle.getCovered());
        } catch (NoSuchElementException e) {
            return Double.NaN;
        }
    }

}
