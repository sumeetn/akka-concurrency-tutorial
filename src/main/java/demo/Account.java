package demo;

/**
 * Created by sumeet on 16/07/16.
 */
public class Account {
    private int balance;
    private int accountNumber;

    public Account(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public synchronized void withdraw(int amount) {
        balance -= amount;
    }

    public synchronized void deposit(int amount) {
        balance += amount;
    }

    public synchronized void transferTo(Account to, int amount) {
        System.out.println("transfering  "+amount +"   from  "+this.accountNumber+"   to "+to.accountNumber);
        this.withdraw(amount);
        to.deposit(amount);
    }

    public int getBalance() {
        return balance;
    }

    public int getAccountNumber(){
        return accountNumber;
    }
}
