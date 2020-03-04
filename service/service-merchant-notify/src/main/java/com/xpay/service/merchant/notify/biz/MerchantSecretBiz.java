package com.xpay.service.merchant.notify.biz;

import com.xpay.api.base.enums.SignTypeEnum;
import com.xpay.facade.merchant.entity.MerchantSecret;
import org.springframework.stereotype.Service;

/**
 * Author: Cmf
 * Date: 2019.12.17
 * Time: 18:06
 * Description:
 */
@Service
public class MerchantSecretBiz {
    //todo 待实现
    private String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOi1G60G5grkxoVltHkqTBXGX1fQu964hUxbIeSxE5egh3o8L0tc2gG+QCe7+FffERiPpr8Adqyan8Srl+W3gzLe/soZBe/R6rVqLutSSfRD6r6yUBR6RL7nP88+smw0tf0XTZZgxY9UsEXY29rR2zfmLtB9Vu11kf6NR9WGGs2jAgMBAAECgYA7pWAoo2IHXMg9nOn7PIov8p6xhYEB/027Woh/c5vP+4d+HzsIGA6Q79DF3nozG6voHbnhrx678w4MfOb8LGNmA2w3oq+WRgh7hFs9EP8IwwvQe0DCkCdqGF4YedHc4+bmV797gTpcLd91iDZ0s/ItBXdsgRWK4CPbYbYzu71hcQJBAPycrw9zSR4W5vnDXoZn39qY5oAwh37aUHTtJFUYu2Dh0l0YSPl/zeFcUuBicgt81BzGqMRAosGRf59nRE0m9J0CQQDr1BWBvHRyb8dLSMA2WPw1jmJ3alnaf3VuBblFEKxR7U8cQ7NBki13qVzU/vU4QiaWtk5NfhtP1YbMsOfBbJc/AkA4hbaF9n29xdIYwKY93LAx8VRiCnnG4IJwJz+h6s7CdsTjH7P0X6xpaIcCvgRWna+4YJsSjoPcW/n6aNjXiofhAkEAvbuhHgL4EQXbard6ZX9MUu5eTEMVTZSUbtNODsHuUe/CYTQaqVupFwf8tyT1N7EJCOJdtz0JTj61uENwAg+S0wJAHeU5zOl0oyJdaEzlX6JvwPMmXawEx0HAcYN4SnFXhahQVTfE6O9ZT8hQ/9ahXong+TOGXbz+GKsMJVor1b2KIw==";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDotRutBuYK5MaFZbR5KkwVxl9X0LveuIVMWyHksROXoId6PC9LXNoBvkAnu/hX3xEYj6a/AHasmp/Eq5flt4My3v7KGQXv0eq1ai7rUkn0Q+q+slAUekS+5z/PPrJsNLX9F02WYMWPVLBF2Nva0ds35i7QfVbtdZH+jUfVhhrNowIDAQAB";


    public MerchantSecret getMerchantSecret(String merchantNo) {
        MerchantSecret merchantSecret = new MerchantSecret();
        merchantSecret.setMerchantNo(merchantNo);
        merchantSecret.setMerchantPublicKey(publicKey);
        merchantSecret.setSignType(SignTypeEnum.RSA.getValue());
        merchantSecret.setPlatformPublicKey(publicKey);
        merchantSecret.setPlatformPrivateKey(privateKey);
        return merchantSecret;
    }
}
