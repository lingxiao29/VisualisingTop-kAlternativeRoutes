package models;

/**
 * Created by llx on 18/02/2018.
 */
public class Parameter {
    private int topK;
    private String source;
    private String target;
    private String method;
    private String[] topkList;

    public Parameter() {
    }

    public Parameter(String[] topkList) {
        this.topkList = topkList;
    }

    public Parameter(int topK, String source, String target, String method, String[] topkList) {
        this.topK = topK;
        this.source = source;
        this.target = target;
        this.method = method;
        this.topkList = topkList;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] getTopkList() {
        return topkList;
    }

    public void setTopkList(String[] topkList) {
        this.topkList = topkList;
    }
}
