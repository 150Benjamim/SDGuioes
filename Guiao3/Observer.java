class Observer implements Runnable {
    private final Bank b;
    private final int expectedBalance;
    private final int[] ins;

    public Observer(Bank b, int expectedBalance, int[] ins) {
        this.b = b;
        this.expectedBalance = expectedBalance;
        this.ins = ins;
    }

    @Override
    public void run() {
        final int balanceOperations = 100000;

        for (int i = 0; i < balanceOperations; i++) {
            int currentBalance = b.totalBalance(ins);
            if (currentBalance != this.expectedBalance) {
                throw new RuntimeException("Unexpected balance: " + currentBalance);
            }
        }
    }
}