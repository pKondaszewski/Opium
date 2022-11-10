package com.example.database;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeAddress {
    private String streetAddress;
    private String postalCode;
    private String country;

    public String toSimpleString() {
        List<String> collection = Stream.of(streetAddress, postalCode, country)
                                     .filter(e -> !Objects.equals(e, ""))
                                     .collect(Collectors.toList());
        return String.join(", ", collection);
    }
}