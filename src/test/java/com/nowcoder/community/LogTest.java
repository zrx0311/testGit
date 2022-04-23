package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sun.reflect.generics.tree.VoidDescriptor;

/**
 * @author zrxggg
 * @create 2022-04-15-19:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LogTest {
    private static final Logger logger = LoggerFactory.getLogger(LogTest.class);
    @Test
    public void logtest1(){
        System.out.println(logger.getName());
        logger.debug("debug_log");
        logger.warn("warn_log");
    }
}
