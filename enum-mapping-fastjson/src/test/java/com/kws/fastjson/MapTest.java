package com.kws.fastjson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author kws
 * @date 2024-01-21 16:48
 */
public class MapTest {


    public static void main(String[] args) {
        Map<Long, Long> map = new HashMap<>();
        map.put(1L, 10L);
        map.put(2L, 20L);

        System.out.println(new ArrayList<>(map.values()));

        Map<Long, List<Long>> params = new HashMap<>();
        for (int i = 10; i < 20; i++) {
            params.computeIfAbsent(1L, key -> new ArrayList<>()).add((long) i);
        }
        System.out.println(params);

        List<String> strs = new ArrayList<>();
        strs.add("1-2");
        strs.add("1-2");
        System.out.println(strs.stream().distinct().collect(Collectors.toList()));



    }
}
