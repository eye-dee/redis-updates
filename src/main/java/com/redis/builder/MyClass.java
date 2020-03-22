package com.redis.builder;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyClass {

    private String name;
    public static void main(String[] args) {
        MyClass m1 = new MyClass();
        MyClass m2 = new MyClass();
        m1.name = m2.name = "m1";

        callMe(m1, m2);
        System.out.println(m1 + "$" + m2);
    }

    private static void callMe(MyClass... m) {
        m[0] = m[1];
        m[1].name = "new name";
    }
}

class Test {

    public static void main(String[] args) {
        I1 x  = a -> a%2 == 0;
        System.out.println(x.checkMagicNumber(10) + " ");
        System.out.println(x.checkMagicNumber(10.0f) + " ");

        NumberComparator c = (a, b) -> {
            if (a > b) return true;
            return false;
        };
        System.out.println(c.compareNumber(15, 10));

        String name = "Hel";
        Runnable r1 = () -> System.out.println(name);
        Runnable r2 = () -> {
            //Thread.sleep(1);
            r1.run();
        };
        r2.run();
    }
}

class Test2 {

    public static void main(String[] args) {
        int[] x = {1, 2, 3, 4, 5};
        increase(x);
        int[] y = {1, 2, 3, 4, 5};
        increase(y[0]);
        System.out.println(x[0] + " " + y[0]);
    }

    private static void increase(int i) {
        i++;
    }

    private static void increase(int[] x) {
        for (int i = 0; i < x.length; i++) {
            x[i]++;
        }
    }
}

class Array {

    public static void main(String[] args) {
        int arr[] = new int[5];
        for (int i = 5; i > 0; i--) {
            arr[5-i] = i;
        }
        Arrays.fill(arr, 1, 4, 8);
        for (int i = 0; i < 5; i++) {
            System.out.print(arr[i]);
        }

    }
}

class Recu {
    int fact(int n) {
        int res;
        if (n == 1)
            return 1;
        res = fact(n - 1) * n;
        return res;
    }
}

class T {

    public static void main(String[] args) {
        final String s = "ABCabc";
        s.chars().forEach(System.out::print);
    }
}

class B {}
class B1 extends B {}
class B2 extends B {}
class ET {

    public static void main(String[] args) {
        B b = new B();
        B1 b1 = new B1();
        B2 b2 = new B2();
        b2 = (B2)b1;
    }
}

class A {

    public static void main(String[] args) {
        Instant ins = Instant.parse("2015-06-25T16:43:30.00z");
        ins.plus(10, ChronoUnit.HOURS);
        System.out.println(ins);

        Duration d = Duration.ofMillis(1100);
        System.out.println(d);
        d = Duration.ofSeconds(61);
        System.out.println(d);

        List<String> strings = Arrays.asList("j", "a", "v", "a");
        String word = strings.stream().reduce("", (a,b) -> a.concat(b));
        System.out.println(word);
        word = strings.stream().collect(Collectors.groupingBy(a -> a)).toString();
        System.out.println(word);
        word = strings.stream().collect(Collectors.joining());
        System.out.println(word);
        word = strings.stream().collect(Collectors.groupingBy(a -> "")).toString();
        System.out.println(word);

    }
}

interface Vg {
    void f();
}

interface HS {
    public abstract Object gg();
}

abstract class f implements Vg, HS {

}

class Out {

    public static void main(String[] args) {
        Recu recu = new Recu();
        System.out.println(recu.fact(6));
    }
}

@FunctionalInterface
interface I1
{
    boolean checkMagicNumbr(int x);
    default boolean checkMagicNumber(float x) {
        return true;
    }
}

@FunctionalInterface
interface NumberComparator {
    boolean compareNumber(int x, int y);
}
