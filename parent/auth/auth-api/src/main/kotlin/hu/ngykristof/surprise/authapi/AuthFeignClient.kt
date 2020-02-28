package hu.ngykristof.surprise.authapi

import org.springframework.cloud.openfeign.FeignClient

@FeignClient("auth-service")
interface AuthFeignClient {}
