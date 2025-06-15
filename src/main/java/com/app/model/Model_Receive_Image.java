package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model_Receive_Image {
    private Long fileID;
    private String image;
    private String fileExtension;
    private int width;
    private int height;
}
