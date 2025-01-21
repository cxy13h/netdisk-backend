package com.example.netdisk.controller;

import com.example.netdisk.service.INetdiskFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/uploadChunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("file") MultipartFile file) {
        if(iNetdiskFileService.checkFileMD5Exist(fileMd5))
            return ResponseEntity.ok("File already uploaded.");
        try {
            return iNetdiskFileService.processChunkUpload(fileMd5, chunkIndex, totalChunks, file);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
