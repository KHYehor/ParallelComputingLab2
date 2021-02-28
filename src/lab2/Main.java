package lab2;
import java.util.concurrent.CountDownLatch;

public class Main {
    public synchronized static void main(String[] args) throws InterruptedException {
        /**
         * Variant20
         * O = SORT(P) * SORT(MR * MS);
         */
        Vector P = new Vector("./task1/P.txt").initWithRandomValues();
        Matrix MR = new Matrix("./task1/MR.txt").initWithRandomValues();
        Matrix MS = new Matrix("./task1/MS.txt").initWithRandomValues();
        // Create locker condition
        CountDownLatch counter1 = new CountDownLatch(1);
        Thread task1 = new Thread(() -> {
            // 1. SORT(P)
            P.sort();
            // unlock the flow
            counter1.countDown();
        });
        // Copy values, because they are variables
        Matrix finalMR = MR;
        Matrix finalMS = MS;
        Thread task2 = new Thread(() -> {
            Matrix Result = finalMR
                    // 2. MR*MS;
                    .multiplyWithMatrix(finalMS)
                    // 3. SORT(MR*MS);
                    .sort();
            // Wait till sorting of P will be finished
            try {
                counter1.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 4. SORT(P) * SORT(MR*MS)
            P.multiplyWithMatrix(Result)
                    .printResult()
                    .saveFinalResult("./task1/O.txt");
        });
        task1.start();
        task2.start();
        // Block main thread to wait other thread finishing their tasks
        task1.join();
        task2.join();
        /**
         * Variant20
         * MG = MB * MS + MC * (MR + MM);
         */
        Matrix MB = new Matrix("./task2/MB.txt").initWithRandomValues();
        MS = new Matrix("./task2/MS.txt").initWithRandomValues();
        Matrix MC = new Matrix("./task2/MC.txt").initWithRandomValues();
        MR = new Matrix("./task2/MR.txt").initWithRandomValues();
        Matrix MM = new Matrix("./task2/MM.txt").initWithRandomValues();
        CountDownLatch counter2 = new CountDownLatch(1);
        // Copy values, because they are variables
        Matrix finalMS1 = MS;
        Thread task3 = new Thread(() -> {
            // 1. MB * MS
            MB.multiplyWithMatrix(finalMS1);
            // unlock the flow
            counter2.countDown();
        });
        Matrix finalMR1 = MR;
        Thread task4 = new Thread(() -> {
            finalMR1
                    // 2. MR + MM
                    .sumWithMatrix(MM)
                    // 3. MC * (MR + MM)
                    .multiplyWithMatrix(MC);
            // Wait till MB * MS will be finished
            try {
                counter2.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finalMR1
                    // 4. (MB * MS) + (MC * (MR + MM))
                    .sumWithMatrix(MB)
                    .printResult()
                    .saveFinalResult("./task2/MG.txt");
        });
        task3.start();
        task4.start();
        // Block main thread to wait other thread finishing their tasks
        task3.join();
        task4.join();
    }
}
