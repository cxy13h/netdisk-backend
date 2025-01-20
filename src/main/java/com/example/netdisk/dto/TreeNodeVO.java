package com.example.netdisk.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNodeVO {

    private Long id; // 节点ID

    private String type; // 节点类型：directory 或 file

    private String name; // 节点名称

    private List<TreeNodeVO> children; // 子节点列表

    public TreeNodeVO(Long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.children = new ArrayList<>();
    }
}
