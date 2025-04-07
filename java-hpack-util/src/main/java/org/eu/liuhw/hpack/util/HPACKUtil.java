package org.eu.liuhw.hpack.util;

import org.eu.liuhw.hpack.modle.HPACKBean;

import java.util.*;
import java.util.function.Function;

/**
 * 将来http header头压缩到key为：
 *
 * @author JavierHouse
 */
public class HPACKUtil {

    private static final Map<String, Integer> staticTable = Collections.unmodifiableMap(
            new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER) {{
                /*put(":authority", 1);
                put(":method", 2);
                put(":method", 3);
                put(":path", 4);
                put(":path", 5);
                put(":scheme", 6);
                put(":scheme", 7);
                put(":status", 8);
                put(":status", 9);
                put(":status", 10);
                put(":status", 11);
                put(":status", 12);
                put(":status", 13);
                put(":status", 14);*/
                put("accept-charset", 15);
                put("accept-encoding", 16);
                put("accept-language", 17);
                put("accept-ranges", 18);
                put("accept", 19);
                put("access-control-allow-origin", 20);
                put("age", 21);
                put("allow", 22);
                put("authorization", 23);
                put("cache-control", 24);
                put("content-disposition", 25);
                put("content-encoding", 26);
                put("content-language", 27);
                put("content-length", 28);
                put("content-location", 29);
                put("content-range", 30);
                put("content-type", 31);
                put("cookie", 32);
                put("date", 33);
                put("etag", 34);
                put("expect", 35);
                put("expires", 36);
                put("from", 37);
                put("host", 38);
                put("if-match", 39);
                put("if-modified-since", 40);
                put("if-none-match", 41);
                put("if-range", 42);
                put("if-unmodified-since", 43);
                put("last-modified", 44);
                put("link", 45);
                put("location", 46);
                put("max-forwards", 47);
                put("proxy-authenticate", 48);
                put("proxy-authorization", 49);
                put("range", 50);
                put("referer", 51);
                put("refresh", 52);
                put("retry-after", 53);
                put("server", 54);
                put("set-cookie", 55);
                put("strict-transport-security", 56);
                put("transfer-encoding", 57);
                put("user-agent", 58);
                put("vary", 59);
                put("via", 60);
                put("www-authenticate", 61);
            }}
    );

    public static Map<String, List<String>> uncompress(String json, Function<String, List<List<String>>> function) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        final List<List<String>> data = function.apply(json);
        for (List<String> list : data) {
            if (list.size() < 2) {

            }
            else if (":".equals(list.get(0))) {
                list.remove(0);
                final String key = list.remove(0);
                for (String k : staticTable.keySet()) {
                    final String v = staticTable.get(k).toString();
                    if (v.equals(key)) {
                        result.put(k, list);
                        break;
                    }
                }

            } else {
                result.put(list.remove(0), list);
            }
        }

        return result;
    }

    public static String compress(Map<String, List<String>> header, Function<List<List<String>>, String> function) {
        HPACKBean result = new HPACKBean();
        for (String key : header.keySet()) {
            final ArrayList<String> list = new ArrayList<>();
            if (staticTable.containsKey(key)) {
                list.add(":");
                list.add(staticTable.get(key).toString());

            }
            else {
                list.add(key);
            }
            list.addAll(header.get(key));
            result.add(list);
        }
        return function.apply(result);
    }
}
