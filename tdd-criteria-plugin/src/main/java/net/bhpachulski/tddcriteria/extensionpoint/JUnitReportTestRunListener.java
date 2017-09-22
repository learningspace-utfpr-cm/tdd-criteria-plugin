package net.bhpachulski.tddcriteria.extensionpoint;

import java.util.Date;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestRunSession;

import net.bhpachulski.tddcriteria.exception.TDDCriteriaException;
import net.bhpachulski.tddcriteria.file.FileUtil;
import net.bhpachulski.tddcriteria.model.TDDCriteriaProjectProperties;
import net.bhpachulski.tddcriteria.model.TestCase;
import net.bhpachulski.tddcriteria.model.TestSuiteSession;
import net.bhpachulski.tddcriteria.network.util.TDDCriteriaNetworkUtil;
import net.bhpachulski.tddcriteria.project.util.TDDCriteriaProjectUtil;

public class JUnitReportTestRunListener extends TestRunListener {

	private TestSuiteSession tss;
	private IProject project;
	private FileUtil futil = new FileUtil();
	private TDDCriteriaNetworkUtil networkUtil = new TDDCriteriaNetworkUtil();
	private TDDCriteriaProjectUtil projectUtil = new TDDCriteriaProjectUtil();
	
	private TDDCriteriaProjectProperties tddCriteriaPropertiesFile;

	@Override
	public void sessionFinished(ITestRunSession session) {
		tss.setFinished(new Date());

		try {			
			futil.generateJUnitTrackFile(getProject(), tss);
			futil.generateSrcTrackFile(getProject());

			Thread.sleep(1000);
			
			networkUtil.sendAllFiles(getCriteriaProjectPropertiesFile(), getProject());
			
		} catch (Exception e) {
			throw new TDDCriteriaException(getProject());
		} finally {
			futil.updateProjectConfigFile(getProject(), 
					getCriteriaProjectPropertiesFile());
		}
		
		super.sessionFinished(session);
	}

	@Override
	public void sessionLaunched(ITestRunSession session) {
		tss = new TestSuiteSession();
		tss.setLaunched(new Date());
		setProject(session.getLaunchedProject().getProject());
		
		setProjectProperties(projectUtil.verifyProjectProperties (getProject()));
		
		super.sessionLaunched(session);
	}

	@Override
	public void sessionStarted(ITestRunSession session) {
		super.sessionStarted(session);
	}

	@Override
	public void testCaseFinished(ITestCaseElement testCaseElement) {
		TestCase tc = new TestCase();
		tc.setClassName(testCaseElement.getTestClassName());
		tc.setMethodName(testCaseElement.getTestMethodName());

		if (testCaseElement.getFailureTrace() != null) {
			tc.setFailDetail(testCaseElement.getFailureTrace());
			tc.setFailed(true);
		}
		
		tss.setTestCases(tc);
		super.testCaseFinished(testCaseElement);
	}

	@Override
	public void testCaseStarted(ITestCaseElement testCaseElement) {
		super.testCaseStarted(testCaseElement);
	}

	public TDDCriteriaProjectProperties getCriteriaProjectPropertiesFile() {
		return tddCriteriaPropertiesFile;
	}

	public void setProjectProperties(TDDCriteriaProjectProperties prop) {
		this.tddCriteriaPropertiesFile = prop;
	}
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
