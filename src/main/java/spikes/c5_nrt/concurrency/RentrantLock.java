package spikes.c5_nrt.concurrency;
import java.util.concurrent.locks.ReentrantLock;

public class RentrantLock {
    public static void main(String[] args) {
        Counter2 counter = new Counter2();
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
        System.out.println("Final Counter::"+counter.getCount());
    }
}

class Counter2 {
    private int count;
    private ReentrantLock lock = new ReentrantLock();

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
