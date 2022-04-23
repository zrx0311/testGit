package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.expression.Ids;

import java.util.Date;
import java.util.List;

/**
 * @author zrxggg
 * @create 2022-04-14-22:25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(1);
        System.out.println(user);
    }
    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,0,10);
        for(DiscussPost dis:list){
            System.out.println(dis);
        }
    }
    @Test
    public void test1(){
        int rows = discussPostService.findDiscussPostRows(101);
        System.out.println(rows);
    }
    @Test
    public void test2(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
}
