package com.example.netdisk.controller;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.netdisk.dto.NetdiskFileInfo;
import com.example.netdisk.entity.NetdiskFile;
import com.example.netdisk.manager.RedisManager;
import com.example.netdisk.service.INetdiskFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 刘潞
 * @since 2025-01-13
 */
@RestController
@RequestMapping("/netdiskFile")
public class NetdiskFileController {

    @Autowired
    private INetdiskFileService iNetdiskFileService;


    /**
     * 处理分片上传
     */
    @PostMapping("/chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunkMd5") String chunkMd5,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("file") MultipartFile file) {
        try {
            iNetdiskFileService.processChunkUpload(fileMd5, chunkMd5, totalChunks, file);
            return ResponseEntity.ok("Chunk upload task submitted.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
