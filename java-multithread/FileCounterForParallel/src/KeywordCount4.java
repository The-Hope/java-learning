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
 * 扫描指定目录下指定关键字的出现次数——生产者-消费者 + 线程池版本实现
 *
 * @author The hope
 * @date 2018/5/20.
 */
public class KeywordCount4 implements KeywordCount {

    private final File directory;
    private final String keyword;
    private ExecutorService pool;

    public KeywordCount4(File directory, String keyword) {
        this.directory = directory;
        this.keyword = keyword;
        pool = Executors.newFixedThreadPool(10);
    }

    public int search() throws InterruptedException, ExecutionException {
        // 准备任务队列
        List<KeywordCounter> fileList = new ArrayList<>();
        searchFiles(directory, fileList, keyword);

        // 开始计算
        List<Future<Integer>> taskList = pool.invokeAll(fileList);

        // 汇总计算信息
        int count = 0;
        for (Future<Integer> task : taskList) {
            count += task.get();
        }
        return count;
    }

    private void searchFiles(final File file, List<KeywordCounter> fileList, String keyword) {
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) searchFiles(subFile, fileList, keyword);
            else {
                fileList.add(new KeywordCounter(subFile, keyword));
            }
        }
    }

    @Override
    public void shutDown() {
        pool.shutdown();
    }

    private static class KeywordCounter implements Callable<Integer> {
        private File file;
        private String keyword;

        KeywordCounter(File file, String keyword) {
            this.file = file;
            this.keyword = keyword;
        }

        @Override
        public Integer call() throws Exception {
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
