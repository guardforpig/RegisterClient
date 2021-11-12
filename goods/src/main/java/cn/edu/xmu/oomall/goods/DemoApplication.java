package cn.edu.xmu.oomall.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("cn.edu.xmu.oomall.goods.mapper")
public class DemoApplication {
    public static void main(String[] args)
    {

        SpringApplication.run(DemoApplication.class,args);
    }
}
