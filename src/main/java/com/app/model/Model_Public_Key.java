package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model_Public_Key {
    private long userID;
    private String dsa_public_key;
    private String rsa_public_key;
}
