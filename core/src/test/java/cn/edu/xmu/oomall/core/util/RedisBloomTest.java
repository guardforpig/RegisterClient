package cn.edu.xmu.oomall.core.util;

import cn.edu.xmu.oomall.core.CoreApplication;
import cn.edu.xmu.oomall.core.bloom.RedisBloomFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = CoreApplication.class)
public class RedisBloomTest {
    @Autowired
    private RedisBloomFilter redisBloomFilter;

    @Test
    void test(){
        redisBloomFilter.bloomFilterDelete("test");

        boolean test1 = redisBloomFilter.bloomFilterExists("test","test");
        assertEquals(false,test1);

        boolean test2 = redisBloomFilter.bloomFilterAdd("test","test");
        assertEquals(true,test2);

        boolean test3 = redisBloomFilter.bloomFilterExists("test","test");
        assertEquals(true,test3);

        redisBloomFilter.bloomFilterDelete("test");
    }
}
