package nl.ou.se.rest.fuzzer.components.reporter.coverage.calculation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CoverageFile {

    // variable(s)
    private Map<String, PhpFile> phpFiles = new HashMap<>();

    // method(s)
    public void merge(CoverageFile report) {
        if (report == null) {
            return;
        }

        this.phpFiles.entrySet().forEach(entry -> {
            entry.getValue().merge(report.getPhpFiles().get(entry.getKey()));
        });
    }

    public Object codeCoveragePercentageFiltered(String filter) {
        Double locExecuted = Double.valueOf(0);
        Double locNotExecuted = Double.valueOf(0);

        for (Entry<String, PhpFile> entry : this.phpFiles.entrySet()) {
            PhpFile phpFile = entry.getValue();
            if (phpFile.path().startsWith(filter)) {
                locExecuted += phpFile.getLinesExecuted().size();
                locNotExecuted += phpFile.getLinesNotExecuted().size();
            }
        }

        if (locExecuted + locNotExecuted == 0) {
            return 0;
        }

        return (locExecuted / (locExecuted + locNotExecuted)) * 100;
    }

    public Integer linesExecuted(String filter) {
        Integer locExecuted = 0;

        for (Entry<String, PhpFile> entry : this.phpFiles.entrySet()) {
            PhpFile phpFile = entry.getValue();
            if (phpFile.path().startsWith(filter)) {
                locExecuted += phpFile.getLinesExecuted().size();
            }
        }

        return locExecuted;
    }
    
    public double codeCoveragePercentage() {
        Double locExecuted = Double.valueOf(0);
        Double locNotExecuted = Double.valueOf(0);
        for (PhpFile phpFile : this.phpFiles.values()) {
            locExecuted += phpFile.getLinesExecuted().size();
            locNotExecuted += phpFile.getLinesNotExecuted().size();
        }

        if (locExecuted + locNotExecuted == 0) {
            return 0;
        }

        return (locExecuted / (locExecuted + locNotExecuted)) * 100;
    }

    public Integer linesExecuted() {
        Integer locExecuted = 0;
        for (PhpFile phpFile : this.phpFiles.values()) {
            locExecuted += phpFile.getLinesExecuted().size();
        }

        return locExecuted;
    }

    public int fileCount() {
        return this.phpFiles.entrySet().size();
    }

    // getter(s) and setter(s)
    public Map<String, PhpFile> getPhpFiles() {
        return phpFiles;
    }

    public void setPhpFiles(Map<String, PhpFile> phpFiles) {
        this.phpFiles = phpFiles;
    }
}