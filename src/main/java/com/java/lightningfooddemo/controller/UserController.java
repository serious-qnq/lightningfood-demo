package com.java.lightningfooddemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.java.lightningfooddemo.common.R;
import com.java.lightningfooddemo.entity.User;
import com.java.lightningfooddemo.service.UserService;
import com.java.lightningfooddemo.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}", code);

            //把验证码放进session
//            session.setAttribute(phone, code);
            //把验证码放进redis
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> sendMsg(@RequestBody Map map, HttpSession session) {
        log.info("map:{}", map);
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取验证码
//        Object attribute = session.getAttribute(phone);
        //从redis 获取缓存验证码
        Object attribute = redisTemplate.opsForValue().get(phone);
        //相比较验证码
        if (attribute != null && attribute.equals(code)) {

            //判断是否为新用户 如果是自动注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            //如果登录成功就在redis缓存中删除验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登陆失败");
    }


    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
