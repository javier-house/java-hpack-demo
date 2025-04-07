package org.eu.liuhw.hpack.filter;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.luben.zstd.Zstd;
import lombok.SneakyThrows;
import org.eu.liuhw.hpack.util.HPACKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * @author JavierHouse
 */
public class TestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TestFilter.class);

    private ObjectMapper objectMapper;

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final Map<String, List<String>> header = parseRequestHeader(request);
        final String original = objectMapper.writeValueAsString(header);

        log.info("原始长度：{}", original.length());
        log.info(original);
        final String originalZstd = zstd(original);
        log.info("原始zstd压缩长度：{}", originalZstd.length());
        final String originalGzip = gzip(original);
        log.info("原始gzip压缩长度：{}", originalGzip.length());
        //final String s11 = zstdDict(s);
        //log.info("普通dict压缩长度：{}", s11.length());
        final String hpack = HPACKUtil.compress(header, new Function<List<List<String>>, String>() {
            @Override
            @SneakyThrows
            public String apply(List<List<String>> maps) {
                return objectMapper.writeValueAsString(maps);
            }
        });


        log.info("HPACK长度：{}, 提升压缩率{}", hpack.length(),NumberUtil.formatPercent(NumberUtil.div((original.length() - hpack.length()), original.length()), 2));
        log.info(hpack);
        final Map<String, List<String>> uncompress = HPACKUtil.uncompress(hpack, new Function<String, List<List<String>>>() {
            @Override
            @SneakyThrows
            public List<List<String>> apply(String s) {
                return objectMapper.readValue(s, new TypeReference<List<List<String>>>() {
                });
            }
        });
        final String uncompressHpack = objectMapper.writeValueAsString(uncompress);
        if (StrUtil.equals(uncompressHpack, original)) {
            log.info("比较一致-----okk");
        }
        else {
            log.warn("比较不一致!!!!!");
        }

        final String hpackZstd = zstd(hpack);
        log.info("HPACK and zstd压缩长度：{}, 提升压缩率{}", hpackZstd.length(), NumberUtil.formatPercent(NumberUtil.div((originalZstd.length() - hpackZstd.length()), originalZstd.length()), 2));
        final String hpackGzip = gzip(hpack);
        log.info("HPACK and gzip压缩长度：{}, 提升压缩率{}", hpackGzip.length(), NumberUtil.formatPercent(NumberUtil.div((originalGzip.length() - hpackGzip.length()), originalGzip.length()), 2));
        //final String s22 = zstdDict(compressStr);
        //log.info("HPACK and dict压缩长度：{}", s22.length());
    }

    private String zstd(String s) {
        final byte[] b1 = Zstd.compress(s.getBytes(StandardCharsets.UTF_8), 3);
        return Base64.getEncoder().encodeToString(b1);
    }


    private String gzip(String s) {
        final byte[] b1 = ZipUtil.gzip(s.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(b1);
    }

    private String zstdDict(String s) {
        final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes1 = ZstdDictGenerator.demo(bytes);
        final byte[] b1 = Zstd.compressUsingDict(bytes, bytes1, 5);
        return Base64.getEncoder().encodeToString(b1);
    }

    /**
     * 获取所有的请求头
     *
     * @param request
     * @return
     */
    protected static Map<String, List<String>> parseRequestHeader(HttpServletRequest request) {
        List<String> list = Collections.list(request.getHeaderNames());
        Map<String, List<String>> result = new HashMap<>();
        for (String k : list) {
            List<String> headers = Collections.list(request.getHeaders(k));
            result.put(k, headers);
        }
        return result;
    }


}
