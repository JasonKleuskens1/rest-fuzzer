package nl.ou.se.rest.fuzzer.components.reporter.responses;

public class DataTableId implements Comparable<DataTableId> {

    // variable(s)
    private Integer numRequests;
    private Integer seconds;

    // constructor(s)
    public DataTableId(Integer numRequests, Integer seconds) {
        this.numRequests = numRequests;
        this.seconds = seconds;
    }

    // method(s)
    public int compareTo(DataTableId other) {
        return this.getNumRequests().compareTo(other.getNumRequests());
    }

    // getter(s) and setter(s)
    public Integer getNumRequests() {
        return numRequests;
    }

    public void setNumRequests(Integer numRequests) {
        this.numRequests = numRequests;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }
}