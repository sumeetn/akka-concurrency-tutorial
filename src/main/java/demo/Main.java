package demo;

public class Main {

    public static void main(String[] args) {
        Account alice = new Account(100);
        alice.deposit(1000);

        Account bob = new Account(200);
        bob.deposit(500);

        new Thread() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println(i + " Thread 1 transferring " + 10);
                    alice.transferTo(bob, 10);
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println(i + " Thread 2 transferring " + 10);
                    bob.transferTo(alice, 10);
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
        try {
            Thread.sleep(1000);
        }catch(Exception ex){

        }
        System.out.println("alice "+alice.getBalance());
    }


}
