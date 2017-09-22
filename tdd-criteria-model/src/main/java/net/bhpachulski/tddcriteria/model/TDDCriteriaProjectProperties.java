package net.bhpachulski.tddcriteria.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TDDCriteriaProjectProperties {

	private Date created = new Date();
	private Student currentStudent;
	private List<StudentFile> sentFiles = new ArrayList<StudentFile>();

	private Date firstIteration;
	private Date secondIteration;
	private Date thirdIteration;
	private Date fourthIteration;
	private Date fifthIteration;
	private Date sixthIteration;

	private String ip = "localhost";

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Student getCurrentStudent() {
		return currentStudent;
	}

	public void setCurrentStudent(Student currentStudent) {
		this.currentStudent = currentStudent;
	}

	public List<StudentFile> getSentFiles() {
		return sentFiles;
	}

	public void setSentFiles(List<StudentFile> sentFiles) {
		this.sentFiles = sentFiles;
	}

	public void setSentFile(StudentFile sentFile) {
		if (sentFiles == null)
			sentFiles = new ArrayList<StudentFile>();

		if (!this.sentFiles.contains(sentFile))
			this.sentFiles.add(sentFile);
	}

	public Date getFirstIteration() {
		return firstIteration;
	}

	public void setFirstIteration(Date firstIteration) {
		this.firstIteration = firstIteration;
	}

	public Date getSecondIteration() {
		return secondIteration;
	}

	public void setSecondIteration(Date secondIteration) {
		this.secondIteration = secondIteration;
	}

	public Date getThirdIteration() {
		return thirdIteration;
	}

	public void setThirdIteration(Date thirdIteration) {
		this.thirdIteration = thirdIteration;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getFourthIteration() {
		return fourthIteration;
	}

	public void setFourthIteration(Date fourthIteration) {
		this.fourthIteration = fourthIteration;
	}

	public Date getFifthIteration() {
		return fifthIteration;
	}

	public void setFifthIteration(Date fifthIteration) {
		this.fifthIteration = fifthIteration;
	}

	public Date getSixthIteration() {
		return sixthIteration;
	}

	public void setSixthIteration(Date sixthIteration) {
		this.sixthIteration = sixthIteration;
	}

}
