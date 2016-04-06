package ua.yandex.bank;

import ua.yandex.misc.NamedThread;
import ua.yandex.utils.Logger;

import java.util.Random;

/**
 * Created by lionell on 4/6/16.
 *
 * @author Ruslan Sakevych
 */
public class Banker {
    private static final Random random = new Random(17);
    private static final int ACCOUNT_COUNT = 10;
    private static final int CUSTOMER_COUNT = 10;
    private static final Bank bank = new Bank();
    private static final Bank.Account[] accounts =
            createAccounts(ACCOUNT_COUNT);

    public static void main(String[] args) {
        printStats();
        int totalAmount = bank.getAmount();
        Logger.logFormat("Total amount: %d", totalAmount);

        Thread[] customerPool = new Thread[CUSTOMER_COUNT];
        for (int i = 0; i < customerPool.length; i++) {
            customerPool[i] = new DummyCustomer();
        }

        for (Thread customer : customerPool) {
            customer.start();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        for (Thread customer : customerPool) {
            customer.interrupt();
        }

        for (Thread customer : customerPool) {
            try {
                customer.join();
            } catch (InterruptedException ignored) {
            }
        }

        printStats();
        int newTotalAmount = bank.getAmount();
        Logger.logFormat("Total amount: %d", newTotalAmount);

        assert totalAmount == newTotalAmount;
    }

    private static Bank.Account[] createAccounts(int amount) {
        Bank.Account[] accounts = new Bank.Account[amount];

        for (int i = 0; i < amount; i++) {
            accounts[i] = createAccount();
        }

        return accounts;
    }

    private static Bank.Account createAccount() {
        return bank.newAccount(random.nextInt(10));
    }

    private static void printStats() {
        Logger.log("Statistics");
        for (int i = 0; i < accounts.length; i++) {
            Logger.log(i + " -> " + accounts[i].getMoney());
        }
    }

    private static class DummyCustomer extends NamedThread {
        private DummyCustomer() {
            super("customer");
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                int from = random.nextInt(accounts.length);
                int to = (from + 1 + random.nextInt(accounts.length - 1))
                        % accounts.length;
                int amount = random.nextInt(10);

                String status = "Transferring $%d from %d to %d... ";

                try {
                    bank.transfer(accounts[from], accounts[to], amount);
                    status += "OK";
                } catch (InterruptedException e) {
                    status += "FAILED";
                    break;
                } finally {
                    Logger.logFormat(status, amount, from, to);
                }

                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
