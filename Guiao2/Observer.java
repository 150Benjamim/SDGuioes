class Observer implements Runnable {
    private Bank b;
    private int expectedBalance;

    public Observer(Bank b, int expectedBalance) {
        this.b = b;
        this.expectedBalance = expectedBalance;
    }

    @Override
    public void run() {
        final int balanceOperations = 100000;

        for (int i = 0; i < balanceOperations; i++) {
            int currentBalance = b.totalBalance();
            if (currentBalance != this.expectedBalance) {
                throw new RuntimeException("Unexpected balance: " + currentBalance);
            }
        }
    }
}