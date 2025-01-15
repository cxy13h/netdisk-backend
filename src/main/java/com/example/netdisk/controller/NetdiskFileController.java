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

    @Autowired
    private RedisManager redisManager;

    @PostMapping("/initialize")
    public ResponseEntity<?> initializeFile(@RequestParam String fileMd5,
                                            @RequestParam String fileName,
                                            @RequestParam int totalChunks) {
        String lockKey = "file_init_lock:" + fileMd5;
        String initializedKey = "file_initialized:" + fileMd5;

        // 1. 查询数据库是否已有该文件
        if(iNetdiskFileService.checkFileMD5Exist(fileMd5)){
            Map<String, Object> response = new HashMap<>();
            response.put("message", "file existed!");
            return ResponseEntity.ok(response);
        }
        // 2. 检查 Redis 是否标记文件已初始化
        if (redisManager.hasKey(initializedKey)) {
            // 再次验证数据库记录，防止 Redis 状态异常
            if(iNetdiskFileService.checkFileMD5Exist(fileMd5)){
                Map<String, Object> response = new HashMap<>();
                response.put("message", "file existed!");
                return ResponseEntity.ok(response);
            }
        }

        // 3. 获取 Redis 锁以防止重复初始化
        boolean lockAcquired = redisManager.tryLock(lockKey, "1", 5, TimeUnit.SECONDS);
        if (!lockAcquired) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("File initialization in progress");
        }

        try {
//            // 4. 初始化文件记录
//            NetdiskFileInfo initNetdiskFileInfo = new NetdiskFileInfo();
//            initNetdiskFileInfo.setFileMd5(fileMd5);
//            initNetdiskFileInfo.setFileName(fileName);
//            initNetdiskFileInfo.setFileSize(fileSize);
//            initNetdiskFileInfo.setTotalChunks(totalChunks);
//            initNetdiskFileInfo.saveFile(fileInfo);
//
//            // 5. 标记文件初始化完成
//            redisTemplate.opsForValue().set(initializedKey, "1", Duration.ofHours(24));
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("fileId", fileInfo.getFileId());
//            response.put("totalChunks", totalChunks);
//            response.put("uploadedChunks", new ArrayList<>()); // 初始时没有已完成的切片
            return ResponseEntity.ok(1);
        } finally {
            // 6. 释放锁
            redisManager.delete(lockKey);
        }
    }



//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            String responseStr = iNetdiskFileService.uploadFile(file);
//            return ResponseEntity.ok("File uploaded successfully: " + responseStr);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
//        }
//    }

//    // 下载文件
//    @GetMapping("/download/{objectName}")
//    public ResponseEntity<?> downloadFile(@PathVariable String objectName) {
//        try {
//            InputStream file = iNetdiskFileService.downloadFile(objectName);
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", "attachment; filename=" + objectName)
//                    .body(file.readAllBytes());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("File download failed: " + e.getMessage());
//        }
//    }
//
//    // 删除文件
//    @DeleteMapping("/delete/{objectName}")
//    public ResponseEntity<String> deleteFile(@PathVariable String objectName) {
//        try {
//            iNetdiskFileService.deleteFile(objectName);
//            return ResponseEntity.ok("File deleted successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("File deletion failed: " + e.getMessage());
//        }
//    }
}
