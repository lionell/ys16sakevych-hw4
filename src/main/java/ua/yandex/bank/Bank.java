package ua.yandex.bank;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lionell on 4/6/16.
 *
 * @author Ruslan Sakevych
 */
public class Bank {
    private final List<Account> accounts = new LinkedList<>();

    public Account newAccount(int amount) {
        Account account = new Account(amount);
        accounts.add(account);

        return account;
    }

    public int getAmount() {
        int sum = 0;

        for (Account account : accounts) {
            sum += account.getMoney();
        }

        return sum;
    }

    public void transfer(Account from, Account to, int amount) throws
            InterruptedException {
        from.withdraw(amount);
        to.deposit(amount);
    }

    public class Account {
        private Lock lock = new ReentrantLock();
        private Condition notEnoughMoney = lock.newCondition();
        private int money;

        public Account(int money) {
            this.money = money;
        }

        private void withdraw(int amount) throws InterruptedException {
            lock.lock();

            try {
                while (amount > money) {
                    notEnoughMoney.await();
                }

                money -= amount;
            } finally {
                lock.unlock();
            }
        }

        private void deposit(int amount) {
            lock.lock();

            money += amount;

            notEnoughMoney.signalAll();
            lock.unlock();
        }

        public int getMoney() {
            return money;
        }
    }
}
