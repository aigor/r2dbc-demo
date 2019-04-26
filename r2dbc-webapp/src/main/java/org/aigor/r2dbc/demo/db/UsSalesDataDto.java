package org.aigor.r2dbc.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsSalesDataDto {
    @Id
    private String code;
    private double sales;
}
