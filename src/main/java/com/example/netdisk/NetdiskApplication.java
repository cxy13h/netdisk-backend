package com.example.netdisk;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.baomidou.mybatisplus.samples.quickstart.mapper")
@MapperScan("com/example/netdisk/dao")
@SpringBootApplication
public class NetdiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetdiskApplication.class, args);
//        FastAutoGenerator.create("jdbc:mysql://localhost:3306/netdisk?serverTimezone=GMT%2B8", "root", "2120516678")
//                .globalConfig(builder -> {
//                    builder.author("刘潞") // 设置作者
//                            .outputDir("src\\main\\java"); // 输出目录
//                })
//                .packageConfig(builder -> {
//                    builder.parent("com.example.netdisk") // 设置父包名
//                            .entity("entity") // 设置实体类包名
//                            .mapper("dao") // 设置 Mapper 接口包名
//                            .service("service") // 设置 Service 接口包名
//                            .serviceImpl("service.impl") // 设置 Service 实现类包名
//                            .xml("mappers"); // 设置 Mapper XML 文件包名
//                })
//                .strategyConfig(builder -> {
//                    builder.addInclude("netdisk_user", "netdisk_file","netdisk_directory","netdisk_user_file") // 设置需要生成的表名
//                            .entityBuilder()
//                            .enableLombok() // 启用 Lombok
//                            .enableTableFieldAnnotation() // 启用字段注解
//                            .controllerBuilder()
//                            .enableRestStyle(); // 启用 REST 风格
//                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
//                .execute(); // 执行生成
    }

}
