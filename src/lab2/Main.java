package lab2;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
        ReentrantLock locker = new ReentrantLock();
        Thread task1 = new Thread(() -> {
            // start locking the flow
            locker.lock();
            // 1. SORT(P)
            P.sort();
            // unlock the flow
            locker.unlock();
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
            locker.lock();
            // 4. SORT(P) * SORT(MR*MS)
            P.multiplyWithMatrix(Result)
                    .printResult()
                    .saveFinalResult("./task1/O.txt");
            // Unlock and leave locker
            locker.unlock();

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
        // Copy values, because they are variables
        Matrix finalMS1 = MS;
        Thread task3 = new Thread(() -> {
            // start locking the flow
            locker.lock();
            // 1. MB * MS
            MB.multiplyWithMatrix(finalMS1);
            // unlock the flow
            locker.unlock();
        });
        Matrix finalMR1 = MR;
        Thread task4 = new Thread(() -> {
            finalMR1
                    // 2. MR + MM
                    .sumWithMatrix(MM)
                    // 3. MC * (MR + MM)
                    .multiplyWithMatrix(MC);
            // Wait till MB * MS will be finished
            locker.lock();
            finalMR1
                    // 4. (MB * MS) + (MC * (MR + MM))
                    .sumWithMatrix(MB)
                    .printResult()
                    .saveFinalResult("./task2/MG.txt");
            // Unlock and leave locker
            locker.unlock();
        });
        task3.start();
        task4.start();
        // Block main thread to wait other thread finishing their tasks
        task3.join();
        task4.join();
    }
}
