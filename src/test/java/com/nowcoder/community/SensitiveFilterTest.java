package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sun.reflect.generics.tree.VoidDescriptor;

/**
 * @author zrxggg
 * @create 2022-04-17-10:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveFilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testSF(){
        String text = "可以吸毒，可以嫖娼，哈哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
