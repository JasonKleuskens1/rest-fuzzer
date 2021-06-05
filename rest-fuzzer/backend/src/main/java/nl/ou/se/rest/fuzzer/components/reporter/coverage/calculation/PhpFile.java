package nl.ou.se.rest.fuzzer.components.reporter.coverage.calculation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PhpFile {

    // variable(s)
    private String name;
    private List<Integer> linesExecuted = new ArrayList<>();
    private List<Integer> linesNotExecuted = new ArrayList<>();

    // method(s)
    public void merge(PhpFile phpFile) {
        if (phpFile == null) {
            return;
        }

        if (!this.getName().equals(phpFile.getName())) {
            throw new IllegalArgumentException("Names should match");
        }

        phpFile.getLinesExecuted().forEach(line -> {
            if (!this.getLinesExecuted().contains(line)) {
                this.getLinesExecuted().add(line);
                this.getLinesNotExecuted().remove(line);
            }
        });
    }

    public double codeCoveragePercentage() {
        Double locExecuted = Double.valueOf(this.getLinesExecuted().size());
        Double locNotExecuted = Double.valueOf(this.getLinesNotExecuted().size());
        return (locExecuted / (locExecuted + locNotExecuted)) * 100;
    }

    public String path() {
        Path path = Paths.get(this.name.replaceAll("\\\\", "/"));
        return path.getParent().toString();
    }

    public String shortName() {
        Path path = Paths.get(this.name.replaceAll("\\\\", "/"));
        return path.getFileName().toString();
    }

    // getter(s) and setter(s)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getLinesExecuted() {
        return linesExecuted;
    }

    public void setLinesExecuted(List<Integer> linesExecuted) {
        this.linesExecuted = linesExecuted;
    }

    public List<Integer> getLinesNotExecuted() {
        return linesNotExecuted;
    }

    public void setLinesNotExecuted(List<Integer> linesNotExecuted) {
        this.linesNotExecuted = linesNotExecuted;
    }
}