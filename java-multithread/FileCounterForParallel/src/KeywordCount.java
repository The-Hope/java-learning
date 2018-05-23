/**
 * Description:
 *  扫描指定目录下指定关键字的出现次数
 * @author The hope
 * @date 2018/5/20.
 */
public interface KeywordCount {

    // 统计函数
    int search() throws Exception;

    // 清理统计过程中使用的相关资源
    void shutDown();
}
