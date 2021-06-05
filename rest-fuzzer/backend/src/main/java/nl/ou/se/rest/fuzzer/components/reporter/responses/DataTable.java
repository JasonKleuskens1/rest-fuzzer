package nl.ou.se.rest.fuzzer.components.reporter.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class DataTable {

    // variable(s)
    private List<Integer> statusCodes;
    private List<List<Object>> dataLines = new ArrayList<>();
    private SortedMap<DataTableId, List<Object[]>> data = new TreeMap<>();

    // constructor(s)
    public DataTable(List<Integer> statusCodes) {
        this.statusCodes = statusCodes;
    }

    // method(s)
    public void add(Integer requests, Integer seconds, List<Object[]> statusCodesAndCounts) {
        this.data.put(new DataTableId(requests, seconds), statusCodesAndCounts);
    }

    public void calculateDataLines() {
        this.dataLines = new ArrayList<>();

        for (Entry<DataTableId, List<Object[]>> entry : this.data.entrySet()) {
            List<Object> dataLine = new ArrayList<>();

            dataLine.add(entry.getKey().getNumRequests()); // requests
            dataLine.add(entry.getKey().getSeconds()); // seconds

            for (Integer statusCode : statusCodes) {
                Integer statusCodeCount = entry.getValue().stream().filter(statusAndCount -> {
                    return statusAndCount[0].equals(statusCode);
                }).mapToInt(statusAndCount -> {
                    return Long.valueOf((long) statusAndCount[1]).intValue();
                }).sum();

                // cumulative response count for current response type
                dataLine.add(statusCodeCount);
            }

            this.dataLines.add(dataLine);
        }
    }

    public Integer getMaxForStatusCode(Integer statusCodeToFind) {
        int index = 0;
        for (Integer statusCode : this.statusCodes) {
            if (statusCode.equals(statusCodeToFind)) {
                break;
            }
            index++;
        }

        List<Object> lastLine = this.dataLines.get(this.dataLines.size() - 1);
        return (Integer) lastLine.get(index + 2); // skip responses and count
    }

    public void reset() {
        this.data.clear();
        this.dataLines.clear();
    }

    // getter(s) and setter(s)
    public List<List<Object>> getDataLines() {
        if (this.dataLines == null || this.dataLines.isEmpty()) {
            this.calculateDataLines();
        }
        return dataLines;
    }

    public void setDataLines(List<List<Object>> dataLines) {
        this.dataLines = dataLines;
    }
}