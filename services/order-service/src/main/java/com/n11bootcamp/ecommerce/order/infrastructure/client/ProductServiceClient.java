package com.n11bootcamp.ecommerce.order.infrastructure.client;

import com.n11bootcamp.ecommerce.order.application.dto.VariantInfo;
import com.n11bootcamp.ecommerce.order.application.port.out.ProductServicePort;
import com.n11bootcamp.ecommerce.order.infrastructure.client.dto.ApiResponse;
import com.n11bootcamp.ecommerce.order.infrastructure.client.dto.VariantInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service", path = "/api")
interface ProductFeignClient {

    @GetMapping("/products/variants/{variantId}")
    ApiResponse<VariantInfoResponse> getVariantInfo(@PathVariable("variantId") UUID variantId);
}

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClient implements ProductServicePort {

    private final ProductFeignClient feignClient;

    @Override
    public VariantInfo getVariantInfo(UUID variantId) {
        log.debug("Product Service'ten varyant bilgisi alınıyor: variantId={}", variantId);
        var response = feignClient.getVariantInfo(variantId).data();
        return new VariantInfo(
                response.productId(),
                response.variantId(),
                response.productName(),
                response.variantValue(),
                response.price(),
                response.active()
        );
    }
}
