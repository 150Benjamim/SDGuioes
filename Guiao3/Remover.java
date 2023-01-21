import java.util.Random;

class Remover implements Runnable {
    Bank b;
    int s; // Number of accounts

    public Remover(Bank b, int s) { this.b=b; this.s=s; }

    public void run() {
        for (int i = 0; i<s; i++) b.closeAccount(i);
    }
}