package com.example.netdisk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author 刘潞
 * @since 2025-01-13
 */
@Data
@TableName("netdisk_user_file")
public class NetdiskUserFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_file_id", type = IdType.AUTO)
    private Long userFileId;

    @TableField("user_id")
    private Long userId;

    @TableField("file_id")
    private String fileId;

    @TableField("directory_id")
    private Long directoryId;

    @TableField("user_filename")
    private String userFilename;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
