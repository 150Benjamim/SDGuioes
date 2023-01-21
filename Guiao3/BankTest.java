import java.util.Random;

class BankTest {
    public static void main(String[] args) throws InterruptedException {

        final int N = 10;
        int[] ins = new int[N];

        Bank b = new Bank();

        for (int i = 0; i < N; i++) {
            b.createAccount(1000);
            ins[i] = i;
        }

        int initialBalance = b.totalBalance(ins);
        System.out.println(initialBalance);

        Thread t1 = new Thread(new Mover(b, N));
        Thread t2 = new Thread(new Mover(b, N));
        //Thread t3 = new Thread(new Observer(b, initialBalance,ins));

        Thread t4 = new Thread(new Remover(b,N));

        t1.start();
        t2.start();
        //t3.start();
        t4.start();

        t1.join();
        t2.join();
        //t3.join();
        t4.join();

        System.out.println(b.totalBalance(ins));

    }



}