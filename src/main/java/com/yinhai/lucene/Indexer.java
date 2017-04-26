package com.yinhai.lucene;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * Desc 生成索引工具类(相当于生成库表)
 * Package: com.lht.lucene
 * User: LIUHUITAO
 * Date: 2017/3/16
 * TIme: 10:14
 */
public abstract class Indexer {
    // 索引存放位置(相当于数据库)
    private Directory directory;

    /**
     * 获取索引writer,写入数据
     * @return IndexWriter
     */
    protected IndexWriter getWriter() throws IOException {
        // 标准分词器
        //Analyzer analyzer = new StandardAnalyzer();
        // 采用中文分词器
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
        return writer;
    }


    public abstract void addIndex(Class<?> targetClass, Object target);
    public abstract void updateIndex(Class<?> targetClass, Object target);
    public abstract void deleteIndex(Class<?> targetClass, Object targetId);
}
