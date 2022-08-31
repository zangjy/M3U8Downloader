package jaygoo.library.m3u8downloader.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jaygoo.library.m3u8downloader.M3U8DownloaderConfig;
import jaygoo.library.m3u8downloader.bean.M3U8;
import jaygoo.library.m3u8downloader.bean.M3U8Ts;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：
 * 创建日期：2017/11/18
 * 描    述: 工具类
 * ================================================
 */

public class MUtils {

    /**
     * 读取M3U8文件头内容(如果需要下载key，则会下载key文件并替换掉原有的网络路径)
     *
     * @param rootPath 和local.m3u8同级
     * @param url      m3u8的地址
     * @return
     * @throws IOException
     */
    public static String parseHeadContent(String rootPath, String url) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                if (line.startsWith("#EXTINF:")) {
                    break;
                } else if (line.startsWith("#EXT-X-KEY")) {
                    Matcher urlMatcher = Pattern.compile("URI=\"(.*?)\"").matcher(line);
                    if (urlMatcher.find() && (urlMatcher.group(1).toLowerCase().startsWith("http://") || urlMatcher.group(1).toLowerCase().startsWith("https://"))) {
                        //没有就创建目录
                        File dir = new File(rootPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        //key的文件名
                        String keyFileName = genUUID() + ".key";
                        //替换key路径
                        line = Pattern.compile("URI=\"(.*?)\"").matcher(line).replaceAll("URI=\"" + "/" + keyFileName + "\"");
                        //下载Key文件
                        InputStream is = new URL(urlMatcher.group(1)).openStream();
                        DataInputStream dis = new DataInputStream(is);
                        FileOutputStream fos = new FileOutputStream(new File(dir, keyFileName));
                        int length;
                        byte[] buffer = new byte[1024];
                        //开始填充数据
                        while ((length = dis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                        is.close();
                        dis.close();
                        fos.close();
                    }
                }
            }
            if (sb.toString().length() > 0) {
                sb.append("\n");
            }
            sb.append(line);
        }
        reader.close();
        return sb.toString().trim();
    }

    /**
     * 将Url转换为M3U8对象
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static M3U8 parseIndex(String url) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        String basepath = url.substring(0, url.lastIndexOf("/") + 1);
        M3U8 ret = new M3U8();
        ret.setBasePath(basepath);
        String line;
        float seconds = 0;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                if (line.startsWith("#EXTINF:")) {
                    line = line.substring(8);
                    if (line.endsWith(",")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    seconds = Float.parseFloat(line);
                }
                continue;
            }
            if (line.endsWith("m3u8")) {
                return parseIndex(basepath + line);
            }
            ret.addTs(new M3U8Ts(line, seconds));
            seconds = 0;
        }
        reader.close();
        return ret;
    }

    /**
     * 清空文件夹
     */
    public static boolean clearDir(File dir) {
        if (dir.exists()) {// 判断文件是否存在
            if (dir.isFile()) {// 判断是否是文件
                return dir.delete();// 删除文件
            } else if (dir.isDirectory()) {// 否则如果它是一个目录
                File[] files = dir.listFiles();// 声明目录下所有的文件 files[];
                for (File file : files) {// 遍历目录下所有的文件
                    clearDir(file);// 把每个文件用这个方法进行迭代
                }
                return dir.delete();// 删除文件夹
            }
        }
        return true;
    }


    private static final float KB = 1024;
    private static final float MB = 1024 * KB;
    private static final float GB = 1024 * MB;

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size >= GB) {
            return String.format(Locale.getDefault(), "%.1f GB", size / GB);
        } else if (size >= MB) {
            float value = size / MB;
            return String.format(value > 100 ? "%.0f MB" : "%.1f MB", value);
        } else if (size >= KB) {
            float value = size / KB;
            return String.format(value > 100 ? "%.0f KB" : "%.1f KB", value);
        } else {
            return String.format(Locale.getDefault(), "%d B", size);
        }
    }

    /**
     * 生成本地m3u8索引文件，ts切片和m3u8文件放在相同目录下即可
     *
     * @param m3u8Dir
     * @param fileName    文件名称
     * @param m3U8        切片信息
     * @param headContent 头部信息
     * @return
     * @throws IOException
     */
    public static File createLocalM3U8(File m3u8Dir, String fileName, M3U8 m3U8, String headContent) throws IOException {
        File m3u8File = new File(m3u8Dir, fileName);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(m3u8File, false));
        bfw.write(headContent + "\n");
        for (M3U8Ts m3U8Ts : m3U8.getTsList()) {
            bfw.write("#EXTINF:" + m3U8Ts.getSeconds() + ",\n");
            bfw.write("/" + m3U8Ts.obtainEncodeTsFileName());
            bfw.newLine();
        }
        bfw.write("#EXT-X-ENDLIST");
        bfw.flush();
        bfw.close();
        return m3u8File;
    }

    public static String getSaveFileDir(String url) {
        return M3U8DownloaderConfig.getSaveDir() + File.separator + MD5Utils.encode(url);
    }

    public static String genUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
