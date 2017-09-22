package net.bhpachulski.tddcriteria.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bhpachulski
 */
public enum TDDStage {

    RED, GREEN, REFACTOR, NONE;
    
    private static Map<String, TDDStage> tddStage;
    
    static {
        tddStage = new HashMap<String, TDDStage>();
        
        for (TDDStage tdd : values()) {
            tddStage.put(tdd.toString(), tdd);
        }
    }
    
    public static TDDStage getStageByString (String s) {
        return tddStage.get(s);
    }
    
}
