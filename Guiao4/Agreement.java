import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Agreement {

    private ReentrantLock l;
    private Condition con;
    private int N; //number of threads
    private int epoch;
    private int times_called;
    private int biggest_value;

    Agreement(int N){
        l = new ReentrantLock();
        con = l.newCondition();
        this.N = N;
        epoch = 0;
        times_called = 0;
        biggest_value = Integer.MIN_VALUE;
    }

    int propose(int choice) throws InterruptedException{
        l.lock();
        try {
            int e = epoch;
            times_called++;
            if (biggest_value<choice) biggest_value = choice;
            if (times_called < N) while (epoch == e){
                con.await();
            }
            else {
                epoch++;
                times_called = 0;
                biggest_value = choice;
                con.signalAll();
            }
            return biggest_value;
        }
        finally {
            l.unlock();
        }

    }


}