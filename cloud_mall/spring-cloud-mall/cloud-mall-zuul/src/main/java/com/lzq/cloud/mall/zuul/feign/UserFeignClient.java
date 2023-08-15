package com.lzq.cloud.mall.zuul.feign;

import com.lzq.cloud.mall.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {
    @PostMapping("/checkAdminRole")
    public Boolean checkAdminRole(@RequestBody User user);
}
