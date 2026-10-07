package com.order.api;

import java.util.stream.IntStream;

public class VirtualThreadClassDemo2 {

     static void main(String[] args) throws InterruptedException {
        var threads = IntStream.range(0, 1000)
                .mapToObj(
                        thCount -> Thread.ofVirtual().unstarted(() -> {
                           //if (thCount == 0) {
                                System.out.println(Thread.currentThread());
                           // }

                        })
                ).toList();
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
