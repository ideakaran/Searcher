package spikes.c5_nrt.concurrency;

public class Synchronized {
    public static void main(String[] args) {
        Counter counter = new Counter();

        Runnable incrementRunnable = () -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
        };

        Thread thread1 = new Thread(incrementRunnable);
        Thread thread2 = new Thread(incrementRunnable);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final count: " + counter.getCount());
    }
}

class Counter {
    private int count;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}