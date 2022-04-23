package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.channels.ReadPendingException;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.nowcoder.community.util.CommunityConstant.*;
import static com.nowcoder.community.util.CommunityConstant.DEFAULT_EXPIRED_SECONDS;

/**
 * @author zrxggg
 * @create 2022-04-15-20:54
 */
@Controller
public class LoginController {
    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    //获取登录页面
    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }
    //激活结果页面
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target","/login");
        }else if (result==ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已经激活过了！");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，您提供的激活码不正确！");
            model.addAttribute("target","/index");
        }
        return "site/operate-result";
    }
    //获取验证码
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        //session.setAttribute("kaptcha",text);

        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);;
        response.addCookie(cookie);
        //存进redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60,TimeUnit.SECONDS);


        //将图片输出给浏览器
        response.setContentType("image/png");  //手动传给前端
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败",e.getMessage());
        }
    }
    //登录
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password,String code, boolean rememberme,
                        Model model,HttpServletResponse response,@CookieValue("kaptchaOwner") String kaptchaOwner){
        //1.检查验证码
        //String kaptcha = (String)session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String)redisTemplate.opsForValue().get(redisKey);
        }
        //String kaptcha = null;
        if (StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }

        // 2.检查账号,密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());//Cookie的key、value都必须是string
            cookie.setPath(contextPath); //有效路径，项目的整个路径下，访问项目的任何页面都有效
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }
    //登出
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

}
