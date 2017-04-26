package com.yinhai.lucene;

import com.yinhai.lucene.factory.SearchRamFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchRamAdapter {
	private SearchRamFactory searchRamFactory;

	/**
	 * 新增索引
	 * @param bean 新增的bean
	 * @param doc_type 新增bean所属的doc类型
	 * @return
	 * @throws IOException
	 */
	public Map addIndex(Map bean,LuceneConstEnum doc_type) throws IOException{
		IndexWriter iwriter = searchRamFactory.getIndexWriter(doc_type);

		FieldType ft = new FieldType();
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		ft.setStored(true);
		ft.setTokenized(true);

		Document doc = new Document();

		Set<String> keySet = bean.keySet();
		Field field;
		for (String key : keySet) {
			field = new Field(key.toString(),bean.get(key)!=null?bean.get(key).toString():"",ft);
			doc.add(field);
		}
		iwriter.addDocument(doc);
		doc = null;
		return null;
	}

	/**
	 * 更新索引
	 * @param bean 更新的bean数据
	 * @param doc_type 更新bean所属的doc类型
	 * @param id 待更新的bean的唯一标识
	 * @param id_name 待更新的bean的唯一标识列名
	 * @return
	 * @throws IOException
	 */
	public Map updateIndex(Map bean,LuceneConstEnum doc_type,String id,String id_name) throws IOException{
        IndexWriter iwriter = searchRamFactory.getIndexWriter(doc_type);

	    FieldType ft = new FieldType();
	    ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
	    ft.setStored(true);
	    ft.setTokenized(true);

    	Document doc = new Document();

    	Set<String> keySet = bean.keySet();
    	Field field;
    	for (String key : keySet) {
    		field = new Field(key.toString(),bean.get(key)!=null?bean.get(key).toString():"",ft);
			doc.add(field);
			field = null;
		}
    	field = null;

    	Term term = new Term(id_name,id);
    	iwriter.updateDocument(term, doc);
    	term = null;

		return null;
	}

	/**
	 * 删除索引
	 * @param doc_type 删除bean所属的doc类型
	 * @param id 待删除的bean的唯一标识
	 * @param id_name 待删除的bean的唯一标识列名
	 * @return
	 * @throws IOException
	 */
	public Map deleteIndex(LuceneConstEnum doc_type,String id,String id_name) throws IOException{
        IndexWriter iwriter = searchRamFactory.getIndexWriter(doc_type);

	    FieldType ft = new FieldType();
	    ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
	    ft.setStored(true);
	    ft.setTokenized(true);

	    Term term = new Term(id_name,id);
    	iwriter.deleteDocuments(term);
    	term = null;

		return null;
	}

	/**
	 * 删除所有索引
	 * @param doc_type 待删除的doc类型
	 * @throws IOException
	 */
	public void deleteAllIndex(LuceneConstEnum doc_type) throws IOException{
        IndexWriter iwriter = searchRamFactory.getIndexWriter(doc_type);
		iwriter.deleteAll();
	}

	/**
	 * 新增索引列表
	 * @param bean 新增的bean
	 * @param doc_type 新增bean所属的doc类型
	 * @return
	 * @throws IOException
	 */
	public Map addAllIndex(List<Map> beanList,LuceneConstEnum doc_type) throws IOException{
		if(beanList!=null&&beanList.size()>0){
            IndexWriter iwriter = searchRamFactory.getIndexWriter(doc_type);

			Document doc;
			for (Map bean : beanList) {
				doc = new Document();
				Set<String> keySet = bean.keySet();
				StringField sf;
				for (String key : keySet) {
					sf = new StringField(key.toString(),bean.get(key)!=null?bean.get(key).toString():"",Store.YES);
					doc.add(sf);
					sf = null;
				}
				sf = null;
				iwriter.addDocument(doc);
				doc = null;
			}
			doc = null;

		}
		return null;
	}

	public SearchRamFactory getSearchRamFactory() {
		return searchRamFactory;
	}

	public void setSearchRamFactory(SearchRamFactory searchRamFactory) {
		this.searchRamFactory = searchRamFactory;
	}


}
