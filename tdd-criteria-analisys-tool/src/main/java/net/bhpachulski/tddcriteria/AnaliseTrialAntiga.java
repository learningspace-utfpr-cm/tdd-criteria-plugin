package net.bhpachulski.tddcriteria;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import net.bhpachulski.tddcriteria.model.eclemma.Counter;
import net.bhpachulski.tddcriteria.model.eclemma.Report;
import net.bhpachulski.tddcriteria.model.eclemma.Type;
import net.bhpachulski.tddcriteria.model.junit.TestSuiteSession;

/**
 *
 * @author bhpachulski
 */
public class AnaliseTrialAntiga {
    
    public static void main(String[] args) throws IOException {
        
        DecimalFormat df = new DecimalFormat("#.00");
        
        JacksonXmlModule module;
        module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        
        ObjectMapper xmlMapper;
        xmlMapper = new XmlMapper(module);
        
        String folderBase = "D:\\runtime-New_configuration\\dsadsadsadsadsa";
        String coverageFile = "/tddCriteria/coverageTrack/";
        String jUnitFile = "/tddCriteria/junitTrack/";
        
        List<File> trials = Arrays.asList(new File(folderBase).listFiles());
        
        for (File fTrial : trials) {
            
            if (fTrial.isDirectory() && !fTrial.getName().startsWith(".")) {
                
                
                for (File fTrialGroup : fTrial.listFiles()) {
                    
                    if (fTrial.isDirectory() && !fTrialGroup.getName().startsWith(".")) {
                        
                        
                        for (File fTrialStudent : fTrialGroup.listFiles()) {
                        
                            if (fTrialStudent.isDirectory() && !fTrialStudent.getName().startsWith(".")) {                                
                                
                                System.out.print(fTrial.getName() + " & ");
                                System.out.print(fTrialGroup.getName() + " & ");
                                System.out.print(fTrialStudent.getName() + " & ");                                
                                
                                for (File fTrialProject : fTrialStudent.listFiles()) {
                                
                                    if (fTrialProject.isDirectory() && !fTrialProject.getName().startsWith(".") && !fTrialProject.getName().equals("Arquivos")) {                                
                                        
                                        
                                        
                                        File fJunit = Arrays.asList(new File(fTrialProject.getAbsolutePath() + jUnitFile).listFiles()).stream().findFirst().get();
                                        TestSuiteSession junitReportFile = xmlMapper.readValue(fJunit, TestSuiteSession.class);

                                        File fCoverage = Arrays.asList(new File(fTrialProject.getAbsolutePath() + coverageFile).listFiles()).stream().findFirst().get();
                                        Report eclemmaReportFile = xmlMapper.readValue(fCoverage, Report.class);
                
                                        Counter instructionsCounter = eclemmaReportFile.getCounter().stream().filter(t -> t.getType() == Type.INSTRUCTION).findFirst().get();
                                        System.out.print(df.format(regraDeTres(instructionsCounter)) + " & ");
                                        
                                        try {
                                            Counter branchsCounter = eclemmaReportFile.getCounter().stream().filter(t -> t.getType() == Type.BRANCH).findFirst().get();
                                            System.out.print(df.format(regraDeTres(branchsCounter)) + " & ");
                                        } catch (Exception e) {System.out.print(" ERRO BRANCH " + fTrialProject.getName().substring( fTrialProject.getName().length() - 1 ) + " & ");}
                                        
                                        Counter methodCounter = eclemmaReportFile.getCounter().stream().filter(t -> t.getType() == Type.METHOD).findFirst().get();
                                        System.out.print((methodCounter.getCovered() + methodCounter.getMissed()) + " & ");
                                        
                                        //qnt casos de teste
                                        System.out.print(junitReportFile.getTestCases().size() + " & ");
                                    }
                                    
                                }
                                
                                System.out.print(" \n");
                                
                            }
                            
                            
                        }
                        
                        
                    }
                    
                }
                
                
            }
            
        }
        
        
    }
    
    public static double regraDeTres(Counter counter) {
        return (100 * counter.getCovered()) / (double) (counter.getMissed() + counter.getCovered());
    }
    
}
