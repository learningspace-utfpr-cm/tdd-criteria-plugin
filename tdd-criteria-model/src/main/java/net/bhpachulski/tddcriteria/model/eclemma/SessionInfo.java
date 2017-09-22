package net.bhpachulski.tddcriteria.model.eclemma;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhpachulski
 */

@XmlRootElement
public class SessionInfo {
    
    @XmlElement()
    private String id;
    
    @XmlElement()
    private Date start;
    
    @XmlElement()
    private Date dump;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getDump() {
        return dump;
    }

    public void setDump(Date dump) {
        this.dump = dump;
    }
    
    
    
}
