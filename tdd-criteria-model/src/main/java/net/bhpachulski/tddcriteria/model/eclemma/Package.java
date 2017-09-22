package net.bhpachulski.tddcriteria.model.eclemma;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhpachulski
 */

@XmlRootElement
public class Package {
    
    @XmlElement()
    private String name;
    
    @XmlElement(name = "class")
    private List<Classs> classs = new ArrayList<>();
    
    @XmlElement()
    private SourceFile sourcefile;
    
    @XmlElement(name = "counter")
    private List<Counter> counter = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Classs> getClasss() {
        return classs;
    }

    public void setClasss(List<Classs> classs) {
        this.classs = classs;
    }

    public SourceFile getSourcefile() {
        return sourcefile;
    }

    public void setSourcefile(SourceFile sourcefile) {
        this.sourcefile = sourcefile;
    }

    public List<Counter> getCounter() {
        return counter;
    }

    public void setCounter(List<Counter> counter) {
        this.counter = counter;
    }

}
