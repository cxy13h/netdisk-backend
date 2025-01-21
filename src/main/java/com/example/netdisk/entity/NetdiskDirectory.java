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
@TableName("netdisk_directory")
public class NetdiskDirectory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "directory_id", type = IdType.AUTO)
    private Long directoryId;

    @TableField("user_id")
    private Long userId;

    @TableField("parent_directory_id")
    private Long parentDirectoryId;

    @TableField("directory_name")
    private String directoryName;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
