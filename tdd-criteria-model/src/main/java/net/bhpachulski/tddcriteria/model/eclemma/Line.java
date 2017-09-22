package net.bhpachulski.tddcriteria.model.eclemma;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhpachulski
 */

@XmlRootElement
public class Line {

    @XmlElement()
    private int nr;
    
    @XmlElement()
    private int mi;
    
    @XmlElement()
    private int ci;
    
    @XmlElement()
    private int mb;
    
    @XmlElement()
    private int cb;    

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public int getMi() {
        return mi;
    }

    public void setMi(int mi) {
        this.mi = mi;
    }

    public int getCi() {
        return ci;
    }

    public void setCi(int ci) {
        this.ci = ci;
    }

    public int getMb() {
        return mb;
    }

    public void setMb(int mb) {
        this.mb = mb;
    }

    public int getCb() {
        return cb;
    }

    public void setCb(int cb) {
        this.cb = cb;
    }
    
    
    
}
