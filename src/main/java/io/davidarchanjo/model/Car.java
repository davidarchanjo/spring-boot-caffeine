package io.davidarchanjo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document
public class Car {

    @Id
    private String id;
    @Indexed(unique = true)
    private String model;
    private int topSpeed;
}
