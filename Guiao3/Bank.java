import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {
        private ReentrantLock acc_lock = new ReentrantLock();
        private int balance;
        Account(int balance) {
            this.balance = balance;
        }
        int balance() {
            return balance;
        }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private ReentrantLock bank_lock = new ReentrantLock();
    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        try {
            bank_lock.lock();
            int id = nextId;
            nextId++;
            map.put(id, c);
            return id;
        }
        finally {
            bank_lock.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        bank_lock.lock();
        Account c = map.remove(id);
        if (c == null) {
            bank_lock.unlock();
            return 0;
        }
        c.acc_lock.lock();
        bank_lock.unlock();
        int balance = c.balance();
        c.acc_lock.unlock();
        return balance;
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        bank_lock.lock();
        Account c = map.get(id);
        if (c == null) {
            bank_lock.unlock();
            return 0;
        }
        c.acc_lock.lock();
        bank_lock.unlock();
        try{
            return c.balance;
        }
        finally {
            c.acc_lock.unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        bank_lock.lock();
        Account c = map.get(id);
        if (c == null) {
            bank_lock.unlock();
            return false;
        }
        c.acc_lock.lock();
        bank_lock.unlock();
        try{
            return c.deposit(value);
        }
        finally {
            c.acc_lock.unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        bank_lock.lock();
        Account c = map.get(id);
        if (c == null) {
            bank_lock.unlock();
            return false;
        }
        c.acc_lock.lock();
        bank_lock.unlock();
        try{
            return c.withdraw(value);
        }
        finally {
            c.acc_lock.unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        bank_lock.lock();
        Account cfrom = map.get(from);
        Account cto = map.get(to);
        if (cfrom == null || cto ==  null) {
            bank_lock.unlock();
            return false;
        }
        cfrom.acc_lock.lock();
        cto.acc_lock.lock();
        bank_lock.unlock();
        boolean withdraw = cfrom.withdraw(value);
        cfrom.acc_lock.unlock();
        boolean deposit = cto.deposit(value);
        cto.acc_lock.unlock();
        return withdraw && deposit;
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        int total = 0;
        ArrayList<Integer> contasLocked = new ArrayList<>(ids.length);
        bank_lock.lock();
        for (int i : ids) {
            if (!map.containsKey(i)) return 0;
            map.get(i).acc_lock.lock();
            contasLocked.add(i);
        }
        bank_lock.unlock();
        for (int i : contasLocked){
            total += map.get(i).balance();
            map.get(i).acc_lock.unlock();
        }
        return total;
    }



}