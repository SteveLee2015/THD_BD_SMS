package thd.bd.sms.crashUtils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件读取工具类
 *
 * @author lerry
 */
public class FileUtils {

    public static void wirteDateTofile(String path, String data) {
        if(TextUtils.isEmpty(data)) return;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file,true);
            try {

                out.write(data.getBytes());
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
