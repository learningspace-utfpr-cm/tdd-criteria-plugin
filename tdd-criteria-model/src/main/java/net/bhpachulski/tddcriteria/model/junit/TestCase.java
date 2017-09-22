package net.bhpachulski.tddcriteria.model.junit;

import java.util.Objects;
import net.bhpachulski.tddcriteria.model.junit.FailureTrace;

public class TestCase {

    private String className;
    private String methodName;
    private String packageName;

    private boolean failed = false;

    private FailureTrace failDetail;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public FailureTrace getFailDetail() {
        return failDetail;
    }

    public void setFailDetail(FailureTrace failDetail) {
        this.failDetail = failDetail;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.methodName);
        hash = 17 * hash + Objects.hashCode(this.packageName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestCase other = (TestCase) obj;
        if (!Objects.equals(this.methodName, other.methodName)) {
            return false;
        }
        if (!Objects.equals(this.packageName, other.packageName)) {
            return false;
        }
        return true;
    }
    
    

}
