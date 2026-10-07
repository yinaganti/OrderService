package com.order.api;

public class VirtualThreadClassDemo {

     static void main(String[] args) throws InterruptedException {


         Thread platforThread = Thread.ofPlatform()
                 .start(()->System.out.println("platform thread ======>"+ Thread.currentThread()));
         Thread virtualThread = Thread.ofVirtual()
                 .start(()->System.out.println("virtual thread ======>"+ Thread.currentThread()));
         platforThread.join();
         virtualThread.join();
         /*
        Thread platformThread = Thread.ofPlatform()
                .start(()-> System.out.println("platform thread ======>"+ Thread.currentThread()));

        Thread virtualThread = Thread.ofVirtual()
                .start(()-> System.out.println("virtual thread ======>"+ Thread.currentThread()));
        platformThread.join();
        virtualThread.join();

          */
    }
}
