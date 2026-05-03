package com.n11bootcamp.ecommerce.payment.infrastructure.iyzico;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreateCheckoutFormInitializeRequest;
import com.iyzipay.request.RetrieveCheckoutFormRequest;
import com.n11bootcamp.ecommerce.payment.application.port.out.IyzicoGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class IyzicoCheckoutFormAdapter implements IyzicoGatewayPort {

    private final Options options;
    private final String callbackUrl;

    public IyzicoCheckoutFormAdapter(
            @Value("${iyzico.api-key}") String apiKey,
            @Value("${iyzico.secret-key}") String secretKey,
            @Value("${iyzico.base-url}") String baseUrl,
            @Value("${iyzico.callback-url}") String callbackUrl
    ) {
        this.options = new Options();
        this.options.setApiKey(apiKey);
        this.options.setSecretKey(secretKey);
        this.options.setBaseUrl(baseUrl);
        this.callbackUrl = callbackUrl;
    }

    @Override
    public IyzicoCheckoutResult initiateCheckout(UUID orderReference, UUID userId,
                                                  BigDecimal amount, String currency) {
        log.info("Iyzico checkout formu başlatılıyor: orderReference={}, amount={}", orderReference, amount);

        try {
            var price = amount.setScale(2, RoundingMode.HALF_UP);
            var request = buildCheckoutFormRequest(orderReference, userId, price);

            var checkoutForm = com.iyzipay.model.CheckoutFormInitialize.create(request, options);
            log.debug("Iyzico yanıtı: status={}, errorCode={}", checkoutForm.getStatus(), checkoutForm.getErrorCode());

            if (!"success".equals(checkoutForm.getStatus())) {
                log.error("Iyzico checkout formu başlatılamadı: errorCode={}, errorMessage={}",
                        checkoutForm.getErrorCode(), checkoutForm.getErrorMessage());
                return new IyzicoCheckoutResult(false, null, null, checkoutForm.getErrorMessage());
            }

            log.info("Iyzico checkout formu oluşturuldu: token={}", checkoutForm.getToken());
            return new IyzicoCheckoutResult(true, checkoutForm.getPaymentPageUrl(),
                    checkoutForm.getToken(), null);

        } catch (Exception e) {
            log.error("Iyzico checkout formu oluşturulurken hata: {}", e.getMessage(), e);
            return new IyzicoCheckoutResult(false, null, null, e.getMessage());
        }
    }

    @Override
    public IyzicoRetrieveResult retrievePaymentResult(String token) {
        log.info("Iyzico ödeme sonucu sorgulanıyor: token={}", token);

        try {
            var request = new RetrieveCheckoutFormRequest();
            request.setLocale(Locale.TR.getValue());
            request.setToken(token);

            var checkoutForm = com.iyzipay.model.CheckoutForm.retrieve(request, options);
            log.debug("Iyzico retrieve yanıtı: paymentStatus={}, errorCode={}",
                    checkoutForm.getPaymentStatus(), checkoutForm.getErrorCode());

            if ("SUCCESS".equals(checkoutForm.getPaymentStatus())) {
                log.info("Iyzico ödeme başarılı: paymentId={}", checkoutForm.getPaymentId());
                return new IyzicoRetrieveResult(true, String.valueOf(checkoutForm.getPaymentId()),
                        buildSuccessJson(checkoutForm), null);
            } else {
                var reason = checkoutForm.getErrorMessage() != null
                        ? checkoutForm.getErrorMessage()
                        : "Ödeme tamamlanamadı: " + checkoutForm.getPaymentStatus();
                log.warn("Iyzico ödeme başarısız: status={}, reason={}", checkoutForm.getPaymentStatus(), reason);
                return new IyzicoRetrieveResult(false, null, null, reason);
            }

        } catch (Exception e) {
            log.error("Iyzico sonuç sorgulama hatası: {}", e.getMessage(), e);
            return new IyzicoRetrieveResult(false, null, null, e.getMessage());
        }
    }

    private CreateCheckoutFormInitializeRequest buildCheckoutFormRequest(
            UUID orderReference, UUID userId, BigDecimal price) {

        var request = new CreateCheckoutFormInitializeRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId(orderReference.toString());
        request.setPrice(price);
        request.setPaidPrice(price);
        request.setCurrency(Currency.TRY.name()); // "TRY"
        request.setBasketId(orderReference.toString());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());
        request.setCallbackUrl(callbackUrl);

        var buyer = new Buyer();
        buyer.setId(userId.toString());
        buyer.setName("Musteri");
        buyer.setSurname("Kullanici");
        buyer.setGsmNumber("+905350000000");
        buyer.setEmail("musteri@example.com");
        buyer.setIdentityNumber("74300864791");
        buyer.setLastLoginDate("2025-01-01 10:00:00");
        buyer.setRegistrationDate("2024-01-01 10:00:00");
        buyer.setRegistrationAddress("Nisantasi, Istanbul");
        buyer.setIp("85.34.78.112");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34340");
        request.setBuyer(buyer);

        var address = new Address();
        address.setContactName("Musteri Kullanici");
        address.setCity("Istanbul");
        address.setCountry("Turkey");
        address.setAddress("Test Teslimat Adresi");
        address.setZipCode("34000");
        request.setShippingAddress(address);
        request.setBillingAddress(address);

        var basketItem = new BasketItem();
        basketItem.setId(orderReference.toString());
        basketItem.setName("E-Ticaret Siparisi");
        basketItem.setCategory1("Genel");
        basketItem.setItemType(BasketItemType.PHYSICAL.name());
        basketItem.setPrice(price);
        request.setBasketItems(List.of(basketItem));

        return request;
    }

    private String buildSuccessJson(com.iyzipay.model.CheckoutForm form) {
        return """
                {"status":"SUCCESS","paymentId":"%s","paymentStatus":"%s","currency":"%s"}
                """.formatted(form.getPaymentId(), form.getPaymentStatus(), form.getCurrency()).trim();
    }
}
