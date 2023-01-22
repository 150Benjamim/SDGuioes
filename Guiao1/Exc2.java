public class Exc2 {

    public static void main(String[] args) throws Exception {

        int N = 10, I = 1000, V = 100;
        Bank b = new Bank();

        Thread[] threads = new Thread[N];

        for (int i = 0; i<10; i++){
            threads[i] = new Thread(()->{
                    for (int j = 0; j<I; j++) b.deposit(V);
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println(b.balance());
    }

}