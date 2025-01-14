package com.example.netdisk.controller;

import com.example.netdisk.service.INetdiskFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String responseStr = iNetdiskFileService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + responseStr);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    // 下载文件
    @GetMapping("/download/{objectName}")
    public ResponseEntity<?> downloadFile(@PathVariable String objectName) {
        try {
            InputStream file = iNetdiskFileService.downloadFile(objectName);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + objectName)
                    .body(file.readAllBytes());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("File download failed: " + e.getMessage());
        }
    }

    // 删除文件
    @DeleteMapping("/delete/{objectName}")
    public ResponseEntity<String> deleteFile(@PathVariable String objectName) {
        try {
            iNetdiskFileService.deleteFile(objectName);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("File deletion failed: " + e.getMessage());
        }
    }
}
