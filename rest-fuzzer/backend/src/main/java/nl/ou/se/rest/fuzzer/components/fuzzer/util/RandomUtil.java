package nl.ou.se.rest.fuzzer.components.fuzzer.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;

public class RandomUtil {

    public static List<RmdParameter> getFromValues(SortedSet<RmdParameter> values, Integer max) {
        List<RmdParameter> list = values.stream().collect(Collectors.toList());

        // no limit
        if (max == null || max.equals(-1)) {
            return list;
        }

        Collections.shuffle(list);
        Integer toIndex = Arrays.asList(list.size(), max).stream().min(Integer::compare).get();
        return list.subList(0, toIndex);
    }

    public static List<String> getFromValues(List<String> values, Integer max) {
        // no limit
        if (max == null || max.equals(-1)) {
            return values;
        }

        Collections.shuffle(values);
        Integer toIndex = Arrays.asList(values.size(), max).stream().min(Integer::compare).get();
        return values.subList(0, toIndex);
    }
}