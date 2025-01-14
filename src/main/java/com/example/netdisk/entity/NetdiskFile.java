package com.example.netdisk.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@Getter
@Setter
@TableName("netdisk_file")
public class NetdiskFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("file_id")
    private String fileId;

    @TableField("md5_hash")
    private String md5Hash;

    @TableField("file_size")
    private Long fileSize;

    @TableField("user_ref_count")
    private Integer userRefCount;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
