package com.example.netdisk.common.constant;

public class RedisKeyConstants {

    // Redis Key 前缀
    public static final String UPLOAD = "upload";

    // 分片已上传状态的后缀
    public static final String UPLOADED_CHUNKS = "uploadedChunks";

    // 分片的后缀
    public static final String CHUNK = "chunk";

    // 合并任务的后缀
    public static final String MERGE = "merge";

    // Redis Key 分隔符
    public static final String SEPARATOR = ":";

    /**
     * 拼接 Redis Key
     * @param parts Redis Key 的各部分
     * @return 拼接后的 Key
     */
    public static String buildKey(String... parts) {
        return String.join(SEPARATOR, parts);
    }
}

