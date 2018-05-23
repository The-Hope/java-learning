import java.io.File;
import java.util.Scanner;

/**
 * Description:
 *  扫描指定目录下指定关键字的出现次数——测试函数
 * @author The hope
 * @date 2018/5/20.
 */
public class KeywordCountTest {

    public static void main(String... args) throws Exception{
        Scanner in = new Scanner(System.in);
        System.out.println("Enter base directory (e.g. C:\\Program Files\\Java\\jdk1.6.0_45\\src): ");
        String directory = in.nextLine();
        System.out.println("Enter keyword (e.g. java): ");
        String keyword = in.nextLine();
        int execTimes = 5;// 设定执行次数

        long start = System.currentTimeMillis();//开始计时

        int totalCount = 0;
        KeywordCount counter = new KeywordCount2(new File(directory), keyword);
        for (int i = 0; i < execTimes; i++) {
            int count = counter.search();
            totalCount += count;
        }

        long end = System.currentTimeMillis();//结束计时
        System.out.println("Statistics: " + totalCount/ execTimes);
        System.out.println("used time: " + (end-start)/ execTimes);

        counter.shutDown();
    }

}
