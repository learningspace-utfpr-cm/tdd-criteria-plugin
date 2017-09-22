package net.bhpachulski.tddcriteria.model.eclemma;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhpachulski
 */
@XmlRootElement
public class Counter {

    @XmlElement()
    private Type type;
    
    @XmlElement()
    private int missed;
    
    @XmlElement()
    private int covered;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public int getCovered() {
        return covered;
    }

    public void setCovered(int covered) {
        this.covered = covered;
    }
}
