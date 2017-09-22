package net.bhpachulski.tddcriteria.model.eclemma;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhpachulski
 */

@XmlRootElement(name = "method")
public class Methodd {
    
    @XmlElement()
    private String name;
    
    @XmlElement()
    private String desc;
    
    @XmlElement()
    private int line;
    
    @XmlElement(name = "counter")
    private List<Counter> counter = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public List<Counter> getCounter() {
        return counter;
    }

    public void setCounter(List<Counter> counter) {
        this.counter = counter;
    }
    
}
