package com.ticketlite.demo.assistant.vector;

import java.util.List;
import java.util.stream.Collectors;

public class PgVectorUtils {
    public static String toPgVectorLiteral(List<Double> vector) {
        return "(" + vector.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }
}
