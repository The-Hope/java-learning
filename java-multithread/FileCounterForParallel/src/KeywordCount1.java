import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Description:
 * 扫描指定目录下指定关键字的出现次数——串行版本实现
 *
 * @author The hope
 * @date 2018/5/20.
 */
public class KeywordCount1 implements KeywordCount {

    private String keyword;
    private File directory;

    public KeywordCount1(File directory, String keyword) {

        this.keyword = keyword;
        this.directory = directory;
    }

    public int search() {
        return search(directory);
    }

    private int search(File directory) {
        int result = 0;
        for (File file : directory.listFiles())
            if (file.isDirectory()) result += search(file);
            else result += count(file);
        return result;
    }

    private int count(File file) {
        int result = 0;
        try (Scanner in = new Scanner(file)) {
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.contains(keyword))
                    result++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void shutDown() {}
}
