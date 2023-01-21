import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BarrierB {

    private ReentrantLock l;
    private Condition con;
    private int N; //number of threads
    private int epoch;
    private int times_called;

    BarrierB(int N){
        l = new ReentrantLock();
        con = l.newCondition();
        this.N = N;
        epoch = 0;
        times_called = 0;
    }

    void await() throws InterruptedException{
        l.lock();
        try {
            int e = epoch;
            times_called++;
            if (times_called < N) while (epoch == e) con.await();
            else {
                times_called = 0;
                epoch++;
                con.signalAll();
            }
        }
        finally {
            l.unlock();
        }
    }

}