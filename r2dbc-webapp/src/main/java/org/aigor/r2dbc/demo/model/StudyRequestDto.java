package org.aigor.r2dbc.demo.model;

import lombok.Value;

@Value
public class StudyRequestDto {
    private final String study;
    private final String region;
    private final String timout;
}
