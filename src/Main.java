import core.*;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Colinhu on 3/23/18.
 */
public class Main {


    public static void main(String[] args) throws InterruptedException, ExecutionException{

        int numThread = 8;//Integer.parseInt(args[1]);
        int numOperation = 1000000;//Integer.parseInt(args[2]);
        BatchQueue<Integer> MyQueue = new BatchQueue<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(numThread);
        long executeTimeBatch = 0;
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < numThread; i++){
            threadPool.submit(new QueueSimulator(numOperation, MyQueue));
        }
        threadPool.shutdown();
        threadPool.awaitTermination(24L, TimeUnit.HOURS);

        long endTime = System.currentTimeMillis();
        executeTimeBatch = endTime - startTime;
        System.out.println(executeTimeBatch);

    }

    private static final class QueueSimulator implements Runnable {
        private final BatchQueue queue;
        private final int numOperation;
        private Random rand;

        public QueueSimulator(int numOperation, BatchQueue queue){
            this.queue = queue;
            this.numOperation = numOperation;
            rand = new Random();
        }


        @Override
        public void run(){
            for(int i = 0; i < numOperation; i++){
                try {
                    if (rand.nextBoolean()) {
                        queue.enq(new Integer(1));
                    } else {
                        queue.deq();
                    }
                }catch (Exception e){
                    //System.out.println(e);
                }
            }
        }
    }
}
