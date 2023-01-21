class BankTest {
    public static void main(String[] args) throws InterruptedException {
        final int N = 10;

        Bank b = new Bank(N);

        for (int i = 0; i < N; i++)
            b.deposit(i, 1000);

        int initialBalance = b.totalBalance();
        System.out.println(initialBalance);

        Thread t1 = new Thread(new Mover(b, 10));
        Thread t2 = new Thread(new Mover(b, 10));
        Thread t3 = new Thread(new Observer(b, initialBalance));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(b.totalBalance());
    }
}
