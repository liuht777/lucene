package com.yinhai.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchAdapter {
    private static LuceneConfig config = LuceneConfig.getIstance();
    private static String doc_base_path;

    static {
        doc_base_path = config.getDocBasePath();
    }

	/**
	 * 新增索引
	 * @param bean 新增的bean
	 * @param doc_type 新增bean所属的doc类型
	 * @return
	 * @throws IOException
	 */
	public static Map addIndex(Map bean,LuceneConstEnum doc_type) throws IOException{
		IndexWriter iwriter = null;
		Directory directory = null;
		try {
			Analyzer analyzer = new SmartChineseAnalyzer();
			
			directory = FSDirectory.open(Paths.get(doc_base_path+doc_type));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			iwriter = new IndexWriter(directory,config);
//			iwriter = SearchFSFactory.getIndexWriter(doc_type);
			
			FieldType ft = new FieldType();
			ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
			ft.setStored(true);
			ft.setTokenized(true);
			
			Document doc = new Document();
			
			Set<String> keySet = bean.keySet();
			for (String key : keySet) {
				doc.add(new Field(key.toString(),bean.get(key)!=null?bean.get(key).toString():"",ft));
			}
			iwriter.addDocument(doc);
			
			iwriter.commit();
			
		}finally{
			iwriter.close();
			directory.close();
		}
		return null;
	}

    /**
     * 新增索引列表
     * @param beanList 新增的beanList
     * @param doc_type 新增bean所属的doc类型
     * @return
     * @throws IOException
     */
    public static Map addAllIndex(List<Map> beanList, LuceneConstEnum doc_type) throws IOException{
        if(beanList!=null&&beanList.size()>0){
            IndexWriter iwriter = null;
            Directory directory = null;
            try {
                Analyzer analyzer = new SmartChineseAnalyzer();
                directory = FSDirectory.open(Paths.get(doc_base_path+doc_type));
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                iwriter = new IndexWriter(directory,config);
//				iwriter = SearchFSFactory.getIndexWriter(doc_type);

                for (Map bean : beanList) {
                    Document doc = new Document();
                    Set<String> keySet = bean.keySet();
                    for (String key : keySet) {
                        doc.add(new StringField(key.toString(),bean.get(key)!=null?bean.get(key).toString():"", Field.Store.YES));
                    }
                    iwriter.addDocument(doc);
                }

                iwriter.commit();

            }finally{
                iwriter.close();
                directory.close();
            }
        }
        return null;
    }

	/**
	 * 更新索引
	 * @param bean 更新的bean数据
	 * @param doc_type 更新bean所属的doc类型
	 * @param id_key 待更新的bean的唯一标识
     * @param id_value 待删除的bean的唯一标识的值
	 * @return
	 * @throws IOException
	 */
	public static Map updateIndex(Map bean,LuceneConstEnum doc_type,String id_key,String id_value) throws IOException{
		IndexWriter iwriter = null;
		Directory directory = null;
		try {
		
			Analyzer analyzer = new StandardAnalyzer();
	
		    directory = FSDirectory.open(Paths.get(doc_base_path+doc_type));
		    IndexWriterConfig config = new IndexWriterConfig(analyzer);
		    iwriter = new IndexWriter(directory,config);
//			iwriter = SearchFSFactory.getIndexWriter(doc_type);

		    FieldType ft = new FieldType();
		    ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		    ft.setStored(true);
		    ft.setTokenized(true);
		    
	    	Document doc = new Document();
	    	
	    	Set<String> keySet = bean.keySet();
	    	for (String key : keySet) {
				doc.add(new Field(key.toString(),bean.get(key)!=null?bean.get(key).toString():"",ft));
			}
	    	iwriter.updateDocument(new Term(id_key,id_value), doc);
	    	
		    iwriter.commit();
		}finally{
			iwriter.close();
			directory.close();
		}
		return null;
	}
	
	/**
	 * 删除索引
	 * @param doc_type 删除bean所属的doc类型
	 * @param id_key 待删除的bean的唯一标识
	 * @param id_value 待删除的bean的唯一标识的值
	 * @return
	 * @throws IOException 
	 */
	public static Map deleteIndex(LuceneConstEnum doc_type,String id_key,String id_value) throws IOException{
		IndexWriter iwriter = null;
		Directory directory = null;
		
		try {
			
			Analyzer analyzer = new StandardAnalyzer();
	
		    directory = FSDirectory.open(Paths.get(doc_base_path+doc_type));
		    IndexWriterConfig config = new IndexWriterConfig(analyzer);
		    iwriter = new IndexWriter(directory,config);
//			iwriter = SearchFSFactory.getIndexWriter(doc_type);
		    
		    FieldType ft = new FieldType();
		    ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		    ft.setStored(true);
		    ft.setTokenized(true);
		    
	    	iwriter.deleteDocuments(new Term(id_key,id_value));
	    	
		    iwriter.commit();
		}finally{
			iwriter.close();
			directory.close();
		}
		return null;
	}
	
	/**
	 * 删除所有索引
	 * @param doc_type 待删除的doc类型
	 * @throws IOException
	 */
	public static void deleteAllIndex(LuceneConstEnum doc_type) throws IOException{
		IndexWriter iwriter = null;
		Directory directory = null;
		
		try {
			Analyzer analyzer = new StandardAnalyzer();
			directory = FSDirectory.open(Paths.get(doc_base_path+doc_type));
			
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			iwriter = new IndexWriter(directory,config);
//			iwriter = SearchFSFactory.getIndexWriter(doc_type);

			iwriter.deleteAll();
			iwriter.commit();
		}finally{
			iwriter.close();
			directory.close();
		}
	}
}
