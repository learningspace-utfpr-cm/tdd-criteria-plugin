package net.bhpachulski.tddcriteria.network.util;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IProject;

import net.bhpachulski.tddcriteria.file.FileUtil;
import net.bhpachulski.tddcriteria.model.FileType;
import net.bhpachulski.tddcriteria.model.StudentFile;
import net.bhpachulski.tddcriteria.model.TDDCriteriaProjectProperties;
import net.bhpachulski.tddcriteria.model.TDDStage;
import net.bhpachulski.tddcriteria.restclient.TDDCriteriaRestClient;

public class TDDCriteriaNetworkUtil {
	
	private FileUtil futil = new FileUtil();
	private TDDCriteriaRestClient restClient = new TDDCriteriaRestClient();

	public void sendAllFiles (TDDCriteriaProjectProperties propertiesFile, IProject project) {
		if (propertiesFile.getCurrentStudent().getId() != null) {
			Map<String, TDDStage> tddProjectStages = futil.readTDDStagesFile(project);
		
			sendFiles(FileType.JUNIT, tddProjectStages, project, propertiesFile);			
			sendFiles(FileType.SRC, tddProjectStages, project, propertiesFile);
			sendFiles(FileType.ECLEMMA, tddProjectStages, project, propertiesFile);
		}
	}
	
	private void sendFiles(FileType ft, Map<String, TDDStage> tddProjectStages, IProject project, TDDCriteriaProjectProperties propertiesFile) {
		try {
			for (File f : futil.getAllFiles (ft, project)) {
				if (!propertiesFile.getSentFiles().contains(new StudentFile(f.getName(), ft))) {
	
					TDDStage fileTDDStage = tddProjectStages.get(FilenameUtils.getBaseName(f.getName().substring(0, f.getName().length() - 1)));
					fileTDDStage = (fileTDDStage == null) ? TDDStage.NONE : fileTDDStage;
					
					StudentFile sf = restClient.sendStudentFile(propertiesFile, 
							project.getName(), futil.getFileAsName(ft, project, f.getName()), ft, fileTDDStage);			
					
					propertiesFile.setSentFile(sf);
				}
			}
		} catch (Exception ex) {
			System.out.println("Erro ao enviar files " + ft);
			ex.printStackTrace();
		}
	}
}
