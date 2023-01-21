import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {

        private int balance;
        private ReentrantLock l = new ReentrantLock();

        Account(int balance) {
            this.balance = balance;
        }
        int balance() {
            return this.balance;
        }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance) return false;
            balance -= value;
            return true;
        }
    }

    // Bank slots and vector of accounts
    private int slots;
    private Account[] av;

    public Bank(int n)
    {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++) av[i]=new Account(0);
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        try {
            av[id].l.lock();
            return av[id].balance();
        }
        finally {
            av[id].l.unlock();
        }
    }

    // Deposit
    boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        try{
            av[id].l.lock();
            return av[id].deposit(value);
        }
        finally {
            av[id].l.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        try{
            av[id].l.lock();
            return av[id].withdraw(value);
        }
        finally {
            av[id].l.unlock();
        }
    }


    public boolean transfer(int from, int to, int value){

        try {
            av[Math.min(from,to)].l.lock();
            av[Math.max(from,to)].l.lock();

            if (!withdraw(from, value)) return false;
            boolean verify = deposit(to, value);
            if (!verify) {
                deposit(from, value);
            }
            return verify;
        }
        finally {
            av[from].l.unlock();
            av[to].l.unlock();
        }
    }

    int totalBalance(){
        int totalBalance = 0;
        for (int i = 0; i < slots; i++) totalBalance += balance(i);
        return totalBalance;
    }





}