package com.yinhai.lucene;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Desc lucene config
 * Package: com.lht.lucene
 * User: LIUHUITAO
 * Date: 2017/3/22
 * TIme: 15:04
 */
public final class LuceneConfig {
    private static final String STORE_TYPE_RAM="RAM";
    private static final String STORE_TYPE_FS="FS";
    private static final String CONFIG_FILE_NAME="lucene.properties";
    private static final String DIRECTORY_KEY="lucene.directory";
    private volatile boolean inited = false;
    /**
     * lucene文件存储的类型
     */
    private final String store_type = STORE_TYPE_FS;
    /**
     * lucene文件存储的位置
     */
    private String directory;

    private LuceneConfig() {
        initConfig();
    }

    private static volatile LuceneConfig instance;

    public static LuceneConfig getIstance() {
        if (instance == null) {
            synchronized (LuceneConfig.class) {
                if (instance == null) {
                    instance = new LuceneConfig();
                }
            }
        }
        return instance;
    }

    private void initConfig() {
        if (inited) {return;}
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        if(in == null){
            throw new RuntimeException("LuceneConfig 初始化失败，未获取到 "+CONFIG_FILE_NAME+ " 文件。");
        }
        Properties pro = new Properties();
        try {
            pro.load(in);
            this.directory = pro.getProperty(DIRECTORY_KEY);
            inited = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final Directory getDirectory() {
        if (store_type.equals(STORE_TYPE_RAM)){
            return new RAMDirectory();
        }else if (store_type.equals(STORE_TYPE_FS)) {
            try {
                return FSDirectory.open(Paths.get(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @return lucene doc root path
     */
    public final String getDocBasePath() {
        return directory;
    }

    public boolean isInited() {
        return inited;
    }
}
