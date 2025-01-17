package com.example.netdisk.common.constant;

public class RedisKeyConstants {

    // Redis Key 前缀
    public static final String PREFIX_UPLOAD = "upload";

    // 分片已上传状态的后缀
    public static final String SUFFIX_UPLOADED_CHUNKS = "uploadedChunks";

    // 文件总分片数的后缀
    public static final String SUFFIX_TOTAL_CHUNKS = "totalChunks";

    // 分片锁的后缀模板
    public static final String SUFFIX_CHUNK_LOCK = "chunk:%s";

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
