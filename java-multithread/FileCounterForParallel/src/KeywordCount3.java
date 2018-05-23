import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description:
 * 扫描指定目录下指定关键字的出现次数——执行器框架版本实现
 *
 * @author The hope
 * @date 2018/5/20.
 */
public class KeywordCount3 implements KeywordCount {


    private File directory;
    private String keyword;
    private ExecutorService pool;

    KeywordCount3(File directory, String keyword) {
        this.directory = directory;
        this.keyword = keyword;
        this.pool = Executors.newCachedThreadPool();
    }

    public int search() throws InterruptedException, ExecutionException {
        // 开始计算
        KeywordCounter keywordCounter = new KeywordCounter(directory, keyword, pool);
        Future<Integer> task = pool.submit(keywordCounter);

        // 结束计算并打印统计信息
        return task.get();
    }

    @Override
    public void shutDown() {
        pool.shutdown();
    }


    private static class KeywordCounter implements Callable<Integer> {
        private File directory;
        private String keyword;
        private ExecutorService pool;

        KeywordCounter(File file, String keyword, ExecutorService pool) {
            this.directory = file;
            this.keyword = keyword;
            this.pool = pool;
        }

        @Override
        public Integer call() throws Exception {
            int count = 0;
            List<Future<Integer>> subTaskList = new ArrayList<>();
            for (File file : directory.listFiles())
                if (file.isDirectory()) {
                    KeywordCounter keywordCounter = new KeywordCounter(file, keyword, pool);
                    Future<Integer> subTask = pool.submit(keywordCounter);
                    subTaskList.add(subTask);
                } else {
                    count += search(file);
                }

            for (Future<Integer> subTask : subTaskList) {
                count += subTask.get();
            }
            return count;
        }

        int search(File file) {
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
