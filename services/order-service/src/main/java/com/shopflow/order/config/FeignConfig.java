package com.shopflow.order.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/** Active la détection des interfaces annotées @FeignClient du package client. */
@Configuration
@EnableFeignClients(basePackages = "com.shopflow.order.client")
public class FeignConfig {
}
