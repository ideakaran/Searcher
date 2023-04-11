package spikes.c5_nrt.concurrency;

import java.util.HashMap;

import static java.lang.Thread.sleep;

public class HashMapExample {
    private HashMap<String, Integer> map = new HashMap<>();

    public HashMapExample() {
        map.put("abc", 22);
    }
    public void addToMap(String key, Integer value) throws InterruptedException {
        synchronized (map) {
            System.out.println("Lock acquired by.." + Thread.currentThread().getName());
            sleep(10000);
            map.put(key, value);
        }
    }

    public  Integer getFromMap(String key) {
//            synchronized (map) {

                System.out.println("Lock acquired by.." + Thread.currentThread().getName());
                return map.get(key);
//            }

    }

    public static void main(String[] args) {
        final HashMapExample example = new HashMapExample();

        // Thread 1 adds to the map
        Thread thread1 = new Thread(() -> {
            try {
                example.addToMap("key1", 1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread 1 added to map");
        });

        // Thread 2 reads from the map
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2 started");
            Integer value = example.getFromMap("key1");
            Integer value2 = example.getFromMap("abc");
            System.out.println("Thread 2 read from map: " + value);
            System.out.println("Thread 2 read from map: " + value2);
        });

        thread1.start();
        thread2.start();
    }
}