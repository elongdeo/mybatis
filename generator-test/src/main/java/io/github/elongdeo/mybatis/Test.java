package io.github.elongdeo.mybatis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author dingyinlong
 * @date 2019/11/13
 */
public class Test {
    private static ThreadLocal<Long> beginTimeThreadLocal = new ThreadLocal<>();
    public static void main(String[] args) {
    }

    public static void testSet() {
    }


    private static void testRate() {
        List<String> list = Arrays.asList("car", "sheep", "sheep");
        Random random = new Random();
        double total = 100000000;
        double notChangeWin = 0;
        double changeWin = 0;
        double invalid = 0;
        for (int i = 0; i < total; i++) {
            int index = random.nextInt(list.size());
            String first = list.get(index);
            List<String> others = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                if (j != index) {
                    others.add(list.get(j));
                }
            }
            String second = others.get(random.nextInt(others.size()));
            if ("car".equals(first)) {
                notChangeWin += 1;
            } else if (!"car".equals(second)) {
                changeWin += 1;
            } else {
                invalid += 1;
            }
        }
        System.out.println("不换赢的概率：" + (notChangeWin / (total - invalid)));
        System.out.println("换赢的概率：" + (changeWin / (total - invalid)));
    }
}
