package com.yinhai.lucene.factory;

import com.yinhai.lucene.LuceneConfig;
import com.yinhai.lucene.LuceneConstEnum;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lucene SearchFSFactory 缓存Directory IndexSearcher
 * @author 刘惠涛
 */
public class SearchFSFactory {
	private static Map<LuceneConstEnum,Directory> directoryMap = new ConcurrentHashMap<LuceneConstEnum,Directory>();
	private static Map<LuceneConstEnum,IndexWriter> indexWriterMap = new ConcurrentHashMap<LuceneConstEnum,IndexWriter>();
	private static Map<LuceneConstEnum,IndexSearcher> indexSearcherMap = new ConcurrentHashMap<LuceneConstEnum,IndexSearcher>();
    private static LuceneConfig config = LuceneConfig.getIstance();

    public SearchFSFactory() {}

	public void init(){
		try {
			String doc_base_path = config.getDocBasePath();
			LuceneConstEnum[] values = LuceneConstEnum.values();
			for (LuceneConstEnum luceneConstEnum : values) {
				FSDirectory fsDirectory = NIOFSDirectory.open(Paths.get(doc_base_path+luceneConstEnum));
				directoryMap.put(luceneConstEnum, fsDirectory);

//              Analyzer analyzer = new SmartChineseAnalyzer();// 中文分词器
//				IndexWriterConfig config = new IndexWriterConfig(analyzer);
//				IndexWriter iwriter = new IndexWriter(fsDirectory, config);
//				indexWriterMap.put(luceneConstEnum, iwriter);
				
				if(fsDirectory.listAll()!=null&&fsDirectory.listAll().length>0){
                    //判断文件夹下是否有索引
                    // 有索引的话就创建IndexSearcher
					DirectoryReader ireader = DirectoryReader.open(fsDirectory);
					IndexSearcher isearcher = new IndexSearcher(ireader);
					indexSearcherMap.put(luceneConstEnum, isearcher);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("SearchFSFactory ");
		}
	}
	
	public IndexSearcher getIndexSearcher(LuceneConstEnum doc_type){
		try {
			if(indexSearcherMap.get(doc_type)==null){
				Directory directory = directoryMap.get(doc_type);
				if(directory.listAll()!=null&&directory.listAll().length>0){
					DirectoryReader ireader = DirectoryReader.open(directory);
					IndexSearcher isearcher = new IndexSearcher(ireader);
					indexSearcherMap.put(doc_type, isearcher);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return indexSearcherMap.get(doc_type);
	}
	
	public IndexWriter getIndexWriter(LuceneConstEnum doc_type){
        return indexWriterMap.get(doc_type);
	}
	
	public void closeIndexWriter(LuceneConstEnum doc_type){
        try {
            indexWriterMap.get(doc_type).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
