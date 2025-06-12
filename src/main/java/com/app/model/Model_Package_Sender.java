package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model_Package_Sender {
    private Long fileID;
    private byte[] data;
    private boolean finish;

}
