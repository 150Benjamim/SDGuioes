import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {

    private Map<String, Product> map =  new HashMap<String, Product>();
    private ReentrantLock w_lock = new ReentrantLock();

    private class Product {
        int quantity = 0;
        private Condition isEmpty = w_lock.newCondition();
    }

    private Product get(String item) {
        w_lock.lock();
        try {
            Product p = map.get(item);
            if (p != null) {
                return p;
            }
            p = new Product();
            map.put(item, p);
            return p;
        }
        finally {
            w_lock.unlock();
        }
    }

    public void supply(String item, int quantity) {
        w_lock.lock();
        Product p = get(item);
        p.quantity += quantity;
        p.isEmpty.signalAll();
        w_lock.unlock();
    }

    // Errado se faltar algum produto...
    public void consume(Set<String> items) throws InterruptedException{
        w_lock.lock();
        boolean allAvailable = false;
        int times_waited = 0;
        while (!allAvailable) {
            allAvailable = true;
            for (String s : items) {
                Product p = get(s);
                while (p.quantity == 0) {
                    allAvailable = false;
                    times_waited++;
                    p.isEmpty.await();
                }
            }
            if (times_waited == 50) break;
        }
        if (times_waited == 50) {
            for (String s : items) {
                Product p = get(s);
                while (p.quantity == 0) p.isEmpty.await();
                p.quantity--;
            }
        }
        else {
            for (String s : items){
                map.get(s).quantity--;
            }
        }
        w_lock.unlock();
    }



        public static void main(String[] args) throws InterruptedException {

        Warehouse w = new Warehouse();

        Thread[] threads = new Thread[4];

        Set<String> f = new HashSet<>();
        f.add("item2");

        Set<String> s = new HashSet<>();
        s.add("item2");

        threads[0] = new Thread(()-> {
            try {
                w.consume(f);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threads[1] = new Thread(()-> {
            try {
                w.consume(s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threads[2] = new Thread(()-> w.supply("item3",5));
        threads[3] = new Thread(()-> w.supply("item2",2));

        for (int i = 0; i<4; i++) threads[i].start();
        for (int i = 0; i<4; i++) threads[i].join();
    }



}