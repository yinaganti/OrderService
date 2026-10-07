package com.order.api;

import java.util.stream.IntStream;

public class VirtualThreadClassDemo3 {

    static void main(String[] args) throws InterruptedException {
        var start = System.currentTimeMillis();
        var threads = IntStream.range(0,100000)
                .mapToObj(thCount -> Thread.ofVirtual().unstarted(()->{}))
                .toList();
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
        var end = System.currentTimeMillis();
        System.out.println(end-start+"ms");
        /*
        var start = System.currentTimeMillis();
        var threads = IntStream.range(0,100000)
                .mapToObj(thCount -> Thread.ofPlatform().unstarted(()->{}))
                .toList();
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
        var end = System.currentTimeMillis();
        System.out.println(end-start+"ms");

         */
    }
}
