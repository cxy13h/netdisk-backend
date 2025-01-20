package com.example.netdisk.controller;

import com.example.netdisk.dto.DirectoryTreeNode;
import com.example.netdisk.dto.TreeNodeVO;
import com.example.netdisk.service.INetdiskUserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 刘潞
 * @since 2025-01-13
 */
@RestController
@RequestMapping("/netdiskUserFile")
public class NetdiskUserFileController {

    @Autowired
    INetdiskUserFileService iNetdiskUserFileService;
    @GetMapping("/tree")
    public TreeNodeVO getDirectoryTree(
            @RequestParam Long userId,
            @RequestParam Long parentDirectoryId) {
        return iNetdiskUserFileService.getDirectoryTree(userId, parentDirectoryId);
    }

}
