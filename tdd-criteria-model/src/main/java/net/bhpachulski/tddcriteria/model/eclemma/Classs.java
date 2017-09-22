package net.bhpachulski.tddcriteria.model.eclemma;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhpachulski
 */
@XmlRootElement(name = "class")
public class Classs {

    @XmlElement
    private String name;

    @XmlElement(name = "method")
    private Methodd methodd;

    @XmlElement(name = "counter")
    private List<Counter> counter = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Methodd getMethodd() {
        return methodd;
    }

    public void setMethodd(Methodd methodd) {
        this.methodd = methodd;
    }

    public List<Counter> getCounter() {
        return counter;
    }

    public void setCounter(List<Counter> counter) {
        this.counter = counter;
    }

}
