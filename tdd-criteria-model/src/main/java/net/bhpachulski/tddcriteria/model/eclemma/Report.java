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
public class Report {
    
    @XmlElement()
    private String name;
    
    @XmlElement()
    private Group group;
    
    @XmlElement()
    private SessionInfo sessioninfo;
    
    @XmlElement(name = "counter")
    private List<Counter> counter = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionInfo getSessioninfo() {
        return sessioninfo;
    }

    public void setSessioninfo(SessionInfo sessioninfo) {
        this.sessioninfo = sessioninfo;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Counter> getCounter() {
        return counter;
    }

    public void setCounter(List<Counter> counter) {
        this.counter = counter;
    }

    
}
