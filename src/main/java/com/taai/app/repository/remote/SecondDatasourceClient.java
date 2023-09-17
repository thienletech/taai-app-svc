package com.taai.app.repository.remote;

import com.taai.app.util.TimeUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@FeignClient(name = "second-datasource-client", url = "${remote.second-datasource.url}")
@Component
public interface SecondDatasourceClient {
    @GetMapping(value = "data/ami_data/{date}/{filename}", produces = "application/zip")
    byte[] downloadFile(
            @PathVariable("date") String date,
            @PathVariable("filename") String filename,
            @RequestHeader("Accept") String accept,
            @RequestHeader("Accept-Encoding") String acceptEncoding,
            @RequestHeader("Host") String host,
            @RequestHeader("Origin") String origin,
            @RequestHeader("Referer") String referer,
            @RequestHeader("Sec-Ch-Ua") String secChUa,
            @RequestHeader("Sec-Ch-Ua-Mobile") String secChUaMobile,
            @RequestHeader("Sec-Ch-Ua-Platform") String secChUaPlatform,
            @RequestHeader("Sec-Fetch-Dest") String secFetchDest,
            @RequestHeader("Sec-Fetch-Mode") String secFetchMode,
            @RequestHeader("User-Agent") String userAgent
    );

    default List<String> getRawStockData(String date, String filename) throws IOException {
        var lines = new ArrayList<String>();

        byte[] zipBytes = downloadFile(
                date,
                filename,
                "*/*",
                "gzip, deflate, br",
                "prs.tvsi.com.vn",
                "http://s.cafef.vn",
                "http://s.cafef.vn/",
                "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"",
                "?0",
                "\"Linux\"",
                "empty",
                "cors",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");
        try (var zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".csv")) {
                    var br = new BufferedReader(new InputStreamReader(zis));
                    String line = br.readLine(); // skip header
                    if (line != null) {
                        while ((line = br.readLine()) != null) {
                            if (StringUtils.hasText(line)) {
                                lines.add(line.trim());
                            }
                        }
                    }
                }
            }
        }

        return lines;
    }

    default List<String> getRawStockData(LocalDate toDate, String filenameFormat) throws IOException {
        var date = TimeUtil.formatDate(toDate, "yyyyMMdd");
        var filename = String.format(filenameFormat, TimeUtil.formatDate(toDate, "ddMMyyyy"));
        return getRawStockData(date, filename);
    }

}
