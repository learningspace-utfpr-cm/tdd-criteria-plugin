package net.bhpachulski.tddcriteria.project.util;

import java.util.UUID;

import org.eclipse.core.resources.IProject;

import net.bhpachulski.tddcriteria.exception.TDDCriteriaException;
import net.bhpachulski.tddcriteria.file.FileUtil;
import net.bhpachulski.tddcriteria.model.Student;
import net.bhpachulski.tddcriteria.model.TDDCriteriaProjectProperties;
import net.bhpachulski.tddcriteria.restclient.TDDCriteriaRestClient;

public class TDDCriteriaProjectUtil {
	
	private FileUtil futil = new FileUtil();
	private TDDCriteriaRestClient restClient = new TDDCriteriaRestClient();
	private static final String STUDENT_NAME = UUID.randomUUID().toString();
	
	public TDDCriteriaProjectProperties verifyProjectProperties (IProject project) {
		try {
			if (futil.projectFileExists(project)) {
				
				TDDCriteriaProjectProperties projectConfigFile = futil.getPropertiesFileAsObject(project);
				
				if (projectConfigFile.getCurrentStudent().getId() == null) {
					Student student = findStudentId(projectConfigFile);
					projectConfigFile.setCurrentStudent(student);
					futil.updateProjectConfigFile(project, projectConfigFile);
				}
				
				return projectConfigFile;
			} else {
				TDDCriteriaProjectProperties criteriaProperties = new TDDCriteriaProjectProperties();
				Student student = findStudentId(criteriaProperties);
				criteriaProperties.setCurrentStudent(student);
				return futil.createProjectConfigFile(project, criteriaProperties);
			}
		} catch (Exception e) {
			throw new TDDCriteriaException(project);
		}				
	}

	private Student findStudentId(TDDCriteriaProjectProperties criteriaProperties) {
		Student student;				
		try {				
			student = restClient.createStudent(criteriaProperties, new Student(STUDENT_NAME));				
		} catch (Exception e) {
			student = new Student(STUDENT_NAME);
		}
		return student;
	}
	
}
