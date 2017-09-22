package net.bhpachulski.tddcriteria.model.junit;

/**
 *
 * @author bhpachulski
 */
public class FailureTrace {
    
    private String expected;
    private String actual;
    private String trace;

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }
    
    
    
}
