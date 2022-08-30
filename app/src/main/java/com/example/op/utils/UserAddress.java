package com.example.op.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAddress {
    private String streetAddress;
    private String postalCode;
    private String country;
}
