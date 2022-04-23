package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.PortUnreachableException;
import java.security.PublicKey;

/**
 * @author zrxggg
 * @create 2022-04-16-22:04
 */
@Controller
@RequestMapping(path = "/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }
    //上传头像
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        //空后缀检查
        String suffix = fileName.substring(fileName.lastIndexOf(".")); //截取文件名后缀
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";

    }
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        //二进制图片，使用字节流
        try (
                FileInputStream fis = new FileInputStream(fileName);//这个输入流需要手动关闭
                OutputStream os = response.getOutputStream();       //response由springmvc管理，自动关闭
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    //修改密码
    @RequestMapping(path = "/updatePassword",method = RequestMethod.POST)
    public String updatePassword(String old_password, String new_password, String confirm_password, Model model){
        User user = hostHolder.getUser();
        //空值处理
        if(StringUtils.isBlank(old_password)){
            model.addAttribute("usernameMsg","原始密码不能为空！");
            return "site/setting";
        }
        if(StringUtils.isBlank(new_password)){
            model.addAttribute("passwordMsg","新密码不能为空！");
            return "site/setting";
        }
        if(StringUtils.isBlank(confirm_password)){
            model.addAttribute("usernameMsg","确认密码不能为空！");
            return "site/setting";
        }
        //老密码正确
        if(!(CommunityUtil.md5(old_password+user.getSalt()).equals(user.getPassword()))){
            model.addAttribute("passwordMsg","原始密码不正确！");
            return "site/setting";
        }
        if(!(new_password.equals(confirm_password))){
            model.addAttribute("passwordMsg","两次密码输入不一致！");
            return "site/setting";
        }
        userService.updatePassword(user.getId(),CommunityUtil.md5(new_password+user.getSalt()));
        return "redirect:/index";
    }

}
