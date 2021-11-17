package cn.edu.xmu.oomall.comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author Ming Qiu
 **/
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core.*", "cn.edu.xmu.oomall.comment.*"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.oomall.comment.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.comment.microservice")
//@EnableDiscoveryClient
public class CommentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentApplication.class, args);
    }

}

