package org.aigor.r2dbc.demo.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import static java.lang.String.format;

@Value
@RequiredArgsConstructor
public class StudyResultDto {
    private final String colorSchema;
    private final Double colorValue;
    private final String pinValue;

    public static StudyResultDto generic(Double colorValue, Object pinValue) {
        return new StudyResultDto("red", colorValue, formatValue(pinValue));
    }

    private static String formatValue(Object pinValue) {
        return (pinValue instanceof Double) ?
            format("%3.1f", pinValue) :
            pinValue.toString();
    }
}
