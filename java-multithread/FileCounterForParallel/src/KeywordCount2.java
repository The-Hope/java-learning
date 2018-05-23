import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:
 * 扫描指定目录下指定关键字的出现次数——多线程+原子变量版本实现
 *
 * @author The hope
 * @date 2018/5/20.
 */
public class KeywordCount2 implements KeywordCount {


    private final File directory;
    private final String keyword;

    KeywordCount2(File directory, String keyword) {

        this.keyword = keyword;
        this.directory = directory;
    }

    public int search() throws InterruptedException {
        Counter counter = new Counter(keyword);
        FileSearch fileSearch = new FileSearch(directory, counter);
        Thread t = new Thread(fileSearch);
        t.start();
        t.join();
        return counter.getCountNum();
    }

    @Override
    public void shutDown() {
    }

    private static class FileSearch implements Runnable {
        private File directory;
        private Counter counter;

        FileSearch(File file, Counter counter) {
            this.directory = file;
            this.counter = counter;
        }

        @Override
        public void run() {
            List<Thread> subThreads = new ArrayList<>();

            for (File file : directory.listFiles())
                if (file.isDirectory()) {
                    FileSearch fileSearch = new FileSearch(file, counter);
                    Thread t = new Thread(fileSearch);
                    subThreads.add(t);
                    t.start();
                } else {
                    counter.search(file);
                }

            for (Thread subThread : subThreads)
                try {
                    subThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    private static class Counter {
        String keyword;
        AtomicInteger count = new AtomicInteger(0);

        Counter(String keyword) {
            this.keyword = keyword;
        }

        int getCountNum() {
            return count.get();
        }

        void search(File file) {
            try (Scanner in = new Scanner(file)) {
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    if (line.contains(keyword))
                        count.incrementAndGet();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
