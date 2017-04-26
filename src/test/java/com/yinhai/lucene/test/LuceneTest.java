package com.yinhai.lucene.test;

import com.yinhai.lucene.LuceneConstEnum;
import com.yinhai.lucene.SearchAdapter;
import com.yinhai.lucene.factory.SearchFSFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Desc Lucene Test
 * Package: com.yinhai.lucene.test
 * User: LIUHUITAO
 * Date: 2017/3/23
 * TIme: 11:13
 */
public class LuceneTest {
    private SearchFSFactory fsFactory;
    @Before
    public void before() {
        fsFactory = new SearchFSFactory();
        fsFactory.init();
    }

    @Test
    public void deleteIndex() {
        try {
            SearchAdapter.deleteAllIndex(LuceneConstEnum.YHCMS_ARTICLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addIndex() {
        List<Map> list = new ArrayList<Map>();
        Map map = new HashMap();
        map.put("id","1");
        map.put("name","张三");
        map.put("content","你好啊,兄弟,我是张三");
        list.add(map);
        map = new HashMap();
        map.put("id","5");
        map.put("name","张流");
        map.put("content","你好啊,兄弟,我是张流");
        list.add(map);
        map = new HashMap();
        map.put("id","6");
        map.put("name","张四");
        map.put("content","你好啊,兄弟,我是张四");
        list.add(map);
        try {
            SearchAdapter.addAllIndex(list, LuceneConstEnum.YHCMS_ARTICLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void update() {
        try {
            Map map = new HashMap();
            map.put("id","1");
            map.put("name","张三风啦");
            map.put("content","你好啊,兄弟,我是张三风啦");
            SearchAdapter.updateIndex(map,LuceneConstEnum.YHCMS_ARTICLE,"id","1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchOne() {
        IndexSearcher searcher = fsFactory.getIndexSearcher(LuceneConstEnum.YHCMS_ARTICLE);
        Assert.assertNotNull(searcher);
        TermQuery query = new TermQuery(new Term("id","1"));
        TopScoreDocCollector docCollector = TopScoreDocCollector.create(1);
        try {
            searcher.search(query,docCollector);
            System.out.println("搜索到"+docCollector.getTotalHits() +"个");
            if(docCollector.getTotalHits() > 0){
                ScoreDoc scoreDoc = docCollector.topDocs().scoreDocs[0];
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("name"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchAll() {
        IndexSearcher searcher = fsFactory.getIndexSearcher(LuceneConstEnum.YHCMS_ARTICLE);
        Assert.assertNotNull(searcher);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser("*",analyzer);
        try {
            Query query = queryParser.parse("*");
            TopDocs docs = searcher.search(query, 20);
            System.out.println("搜索到"+docs.totalHits +"个");
            for(ScoreDoc scoreDoc:docs.scoreDocs){
                Document doc=searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("name"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void searchMany() {

    }

    @Test
    public void searchComplicated() {

    }
}
