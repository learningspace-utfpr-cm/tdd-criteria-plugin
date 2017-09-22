package net.bhpachulski.tddcriteria;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.io.Files;

import net.bhpachulski.tddcriteria.model.analysis.TDDCriteriaProjectSnapshot;
import net.bhpachulski.tddcriteria.model.eclemma.Counter;
import net.bhpachulski.tddcriteria.model.eclemma.Report;
import net.bhpachulski.tddcriteria.model.junit.TestSuiteSession;

/**
 *
 * @author bhpachulski
 */
public class Main {
    
    public static void main(String[] args) throws IOException, ParseException {
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_M_d_HH_mm");
        SimpleDateFormat sdfShow = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        String jUnitFolderPath = "D:/runtime-New_configuration/dsadsadsadsadsa/tddCriteria/junitTrack/";
        String EclemmaFolderPath = "D:/runtime-New_configuration/dsadsadsadsadsa/tddCriteria/coverageTrack/";
        
        JacksonXmlModule module;
        module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        
        ObjectMapper xmlMapper;
        xmlMapper = new XmlMapper(module);
        
        Map<Date, TDDCriteriaProjectSnapshot> projectTimeLine = new HashMap<>();
        
        List<File> jUnitFiles = Arrays.asList(new File(jUnitFolderPath).listFiles());
        for (File jUnitFile : jUnitFiles) {
            TestSuiteSession tss = xmlMapper.readValue(jUnitFile, TestSuiteSession.class);
            
            TDDCriteriaProjectSnapshot snapshotPutJunit = new TDDCriteriaProjectSnapshot();
            snapshotPutJunit.setjUnitSession(tss);
            
            projectTimeLine.put(sdf.parse(Files.getNameWithoutExtension(jUnitFile.getName()).substring(0, 15)), snapshotPutJunit);
        }        
        
        List<File> eclemmaFiles = Arrays.asList(new File(EclemmaFolderPath).listFiles());
        for (File eclemmaFile : eclemmaFiles) {
            Report rep = xmlMapper.readValue(eclemmaFile, Report.class);
            
            projectTimeLine.get(sdf.parse(Files.getNameWithoutExtension(eclemmaFile.getName()).substring(0, 15))).setEclemmaSession(rep);            
        }
        
        int cont = 1;
        for (Map.Entry<Date, TDDCriteriaProjectSnapshot> entrySet : projectTimeLine.entrySet()) {
            Date key = entrySet.getKey();
            TDDCriteriaProjectSnapshot value = entrySet.getValue();
            
            System.out.print(cont);
            System.out.print(";");
            System.out.print(sdfShow.format(key));
            System.out.print(";");
            
            //qnt casos de teste
            System.out.print(value.getjUnitSession().getTestCases().size());
            System.out.print(";");
            
            //qnt casos de teste passando
            System.out.print(value.getjUnitSession().getTestCases().stream().filter(t -> t.getFailDetail() == null).count());
            System.out.print(";");

            //qnt casos de teste falhando
            System.out.print(value.getjUnitSession().getTestCases().stream().filter(t -> t.getFailDetail() != null).count());
            System.out.print(";");

            if (value.getEclemmaSession() != null) {
                for (Counter counter : value.getEclemmaSession().getCounter()) {
                    System.out.print(counter.getCovered());
                    System.out.print(";");
                    
                    System.out.print(counter.getMissed());
                    System.out.print(";");
                }
            }

            System.out.print("\n");
            
            cont++;
        }
        

    }
    
}
