package net.bhpachulski.tddcriteria.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IProject;

import net.bhpachulski.tddcriteria.file.FileUtil;

public class TDDCriteriaException extends RuntimeException {
	
	private static final long serialVersionUID = 23L;
	private FileUtil fu = new FileUtil();

	public TDDCriteriaException(IProject p) {
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		fu.createTxtFile(p, exceptionAsString);
	}	
	
}
