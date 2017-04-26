package com.yinhai.lucene.factory;

import com.yinhai.lucene.LuceneConstEnum;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lucene SearchRamFactory 缓存Directory IndexSearcher
 * @author 刘惠涛
 */
public class SearchRamFactory {
	private Map<LuceneConstEnum,Directory> directoryMap = new ConcurrentHashMap<LuceneConstEnum,Directory>();
	private Map<LuceneConstEnum,IndexWriter> indexWriterMap = new ConcurrentHashMap<LuceneConstEnum,IndexWriter>();
	private Map<LuceneConstEnum,SearcherManager> searcherManagerMap = new ConcurrentHashMap<LuceneConstEnum,SearcherManager>();

	public void init() throws IOException{
		LuceneConstEnum[] values = LuceneConstEnum.values();
		for (LuceneConstEnum luceneConstEnum : values) {
			Directory directory = new RAMDirectory();
			directoryMap.put(luceneConstEnum, directory);
			
			IndexWriterConfig iwc = new IndexWriterConfig(new KeywordAnalyzer());
			IndexWriter indexWriter = new IndexWriter(directory, iwc);
			indexWriterMap.put(luceneConstEnum, indexWriter);
			
			SearcherManager searcherManager = new SearcherManager(indexWriter, true, true, new SearcherFactory());
			
			ControlledRealTimeReopenThread<IndexSearcher> ct = new ControlledRealTimeReopenThread<IndexSearcher>(indexWriter, searcherManager, 5.0, 0.025);
			ct.setDaemon(true);
			ct.setName(luceneConstEnum+"--lucene后台刷新");
			ct.start();
			
			searcherManagerMap.put(luceneConstEnum, searcherManager);
		}
		
	}
	
	public IndexWriter getIndexWriter(LuceneConstEnum doc_type){
        return indexWriterMap.get(doc_type);
	}
	
	public IndexSearcher getIndexSearcher(LuceneConstEnum doc_type) throws IOException{
		return searcherManagerMap.get(doc_type).acquire();
	}
	
	public void relaseSearcher(LuceneConstEnum doc_type,IndexSearcher indexSearcher) throws IOException{
		searcherManagerMap.get(doc_type).release(indexSearcher);
	}
	
}
