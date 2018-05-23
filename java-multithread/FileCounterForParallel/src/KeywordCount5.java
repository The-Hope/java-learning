import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description:
 * 扫描指定目录下指定关键字的出现次数——生产者-消费者 + 阻塞队列 + 线程池版本实现
 *
 * @author The hope
 * @date 2018/5/20.
 */
class KeywordCount5 implements KeywordCount {

    private static final int PARALLEL_TASK_SIZE = 5;
    private static final int FILE_QUEUE_SIZE = 10;

    private final File directory;
    private final String keyword;
    private ExecutorService pool;

    KeywordCount5(File directory, String keyword) {
        this.keyword = keyword;
        this.directory = directory;
        pool = Executors.newCachedThreadPool();
    }

    public int search() throws InterruptedException, ExecutionException {
        // 准备任务队列
        BlockingQueue<File> taskQueue = new ArrayBlockingQueue<>(FILE_QUEUE_SIZE);
        FileEnumTask produceTask = new FileEnumTask(directory, taskQueue);
        pool.submit(produceTask);

        // 开始计算
        Future<Integer>[] taskArray = new Future[PARALLEL_TASK_SIZE];
        for (int i = 0; i < PARALLEL_TASK_SIZE; i++) {
            taskArray[i] = pool.submit(new KeywordCounter(keyword, taskQueue));
        }
        // 结束计算并打印统计信息
        int count = 0;
        for (Future<Integer> task : taskArray) {
            count += task.get();
        }
        return count;
    }

    @Override
    public void shutDown() {
        pool.shutdown();
    }

    private static class FileEnumTask implements Runnable {
        static File DUMMY = new File("");

        private File startDirectory;
        private BlockingQueue<File> taskQueue;


        FileEnumTask(File directory, BlockingQueue<File> taskQueue) {
            this.startDirectory = directory;
            this.taskQueue = taskQueue;
        }

        @Override
        public void run() {
            try {
                enumerate(startDirectory);
                taskQueue.put(DUMMY);
            } catch (InterruptedException ignored) {
            }
        }

        private void enumerate(File file) throws InterruptedException {
            for (File subFile : file.listFiles()) {
                if (subFile.isDirectory()) enumerate(subFile);
                else taskQueue.put(subFile);
            }
        }
    }

    private static class KeywordCounter implements Callable<Integer> {
        private final BlockingQueue<File> taskQueue;
        private String keyword;

        KeywordCounter(String keyword, BlockingQueue<File> taskQueue) {
            this.keyword = keyword;
            this.taskQueue = taskQueue;
        }

        @Override
        public Integer call() throws Exception {
            int result = 0;
            boolean isDone = false;
            while (!isDone) {
                File file = taskQueue.take();
                if (FileEnumTask.DUMMY == file) {
                    taskQueue.put(FileEnumTask.DUMMY);
                    isDone = true;
                } else {
                    result += count(file);
                }
            }
            return result;
        }

        private Integer count(File file) {
            int count = 0;
            try (Scanner in = new Scanner(file)) {
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    if (line.contains(keyword))
                        count++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return count;
        }

    }

}
