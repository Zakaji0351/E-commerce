package com.lzq.cloud.mall.cartorder.feign;

import com.lzq.cloud.mall.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {
    @GetMapping("/getUser")
    User getUser();
}
