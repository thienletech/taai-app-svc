package com.taai.app.repository.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "first-datasource-client", url = "${remote.first-datasource.url}")
@Component
public interface FirstDatasourceClient {
    @PostMapping("/get-detail-stock-by")
    String getDetailStockByType(
            @RequestParam("type") String type,
            @RequestParam("typeTab") String typeTab,
            @RequestParam("sort") String sort,
            @RequestParam("stockStr") String stockStr,
            @RequestHeader("Accept") String accept,
            @RequestHeader("Accept-Encoding") String acceptEncoding,
            @RequestHeader("Accept-Language") String acceptLanguage,
            @RequestHeader("Content-Type") String contentType,
            @RequestHeader("Host") String host,
            @RequestHeader("Origin") String origin,
            @RequestHeader("Referer") String referer,
            @RequestHeader("Sec-Ch-Ua") String secChUa,
            @RequestHeader("Sec-Ch-Ua-Mobile") String secChUaMobile,
            @RequestHeader("Sec-Ch-Ua-Platform") String secChUaPlatform,
            @RequestHeader("Sec-Fetch-Dest") String secFetchDest,
            @RequestHeader("Sec-Fetch-Mode") String secFetchMode,
            @RequestHeader("User-Agent") String userAgent,
            @RequestHeader("X-Requested-With") String xRequestedWith
    );

    default String getDetailStockByType(String type) {
        return getDetailStockByType(
                type,
                "M",
                "SortName-up",
                "null",
                "*/*",
                "gzip, deflate, br",
                "en-US,en;q=0.9",
                "application/x-www-form-urlencoded; charset=UTF-8",
                "prs.tvsi.com.vn",
                "https://prs.tvsi.com.vn",
                "https://prs.tvsi.com.vn/",
                "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"",
                "?0",
                "\"Linux\"",
                "empty",
                "cors",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
                "XMLHttpRequest"
        );
    }
}
