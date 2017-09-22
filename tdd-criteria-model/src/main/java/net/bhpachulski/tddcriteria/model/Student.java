package net.bhpachulski.tddcriteria.model;

public class Student {

	private Integer id;
	private String name;
	private Boolean excluido;
	private ExperimentalGroup experimentalType;

	public Student() {
	}

	public Student(String name) {
		this.setName(name);
	}

	public Boolean isExcluido() {
		return this.excluido;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getExcluido() {
		return excluido;
	}

	public void setExcluido(Boolean excluido) {
		this.excluido = excluido;
	}

	public ExperimentalGroup getExperimentalType() {
		return experimentalType;
	}

	public void setExperimentalType(ExperimentalGroup experimentalType) {
		this.experimentalType = experimentalType;
	}

}