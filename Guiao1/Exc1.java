import java.time.Duration;
import java.time.Instant;

public class Exc1 {

    public static void main(String[] args) throws Exception {

        Thread[] threads = new Thread[10];

        for (int i = 0; i<10; i++){
            threads[i] = new Thread(new WriteTillI());
            threads[i].start();
        }

        for (Thread t : threads) t.join();

        System.out.println("Fim");
    }

}