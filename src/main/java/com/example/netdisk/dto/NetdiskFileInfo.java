package com.example.netdisk.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NetdiskFileInfo {

    private String fileId;

    private Long fileSize;

    private LocalDateTime createdAt;
}
