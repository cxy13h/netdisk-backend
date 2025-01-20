package com.example.netdisk.dto;
import lombok.Data;

@Data
public class DirectoryTreeNode {

    private Long id; // 节点ID

    private Long parentDirectoryId; // 父目录ID

    private String type; // 节点类型：directory 或 file

    private String name; // 节点名称
}
