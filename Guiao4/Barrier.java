import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {

    private ReentrantLock l;
    private Condition con;
    private int N; //number of threads
    private int times_called;

    Barrier(int N){
        l = new ReentrantLock();
        con = l.newCondition();
        this.N = N;
        times_called = 0;
    }

    void await() throws InterruptedException{
        l.lock();
        try {
            times_called++;
            if (times_called < N) while (times_called < N) con.await();
            else con.signalAll();
        }
        finally {
            l.unlock();
        }
    }

}