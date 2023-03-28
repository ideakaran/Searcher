package spikes.c5_nrt;

// The SearcherManager is a utility class that facilitates the sharing of IndexSearcher
// across multiple threads. It provides the facilities to acquire and release IndexSearcher,
// while allowing IndexSearcher to be reopened periodically. The IndexSearcher attribute
// can be refreshed by calling maybeRefresh prior to acquire, though it's recommended
// that this method be called periodically in a separate thread to minimize impacting a
// query that happens to trigger a refresh. Note that it's very important to call release so
// SearcherManager knows when IndexSearcher is not in use so that it can be closed
// for a refresh.
//
// The benefit of using SearcherManager is that it handles the management of the
//IndexSearcher refreshes internally and lets you share a single IndexSearcher across
//multiple threads. The acquire method should always return an IndexSearcher so that it
//simplifies the instantiation logic.

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class SearchManager {

    public static void main(String[] args) throws IOException, ParseException {
        Directory directory = FSDirectory.open(new File("data/index").toPath());
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        SearcherManager searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory());

        //add documents to index here
        Document doc = new Document();
        TextField textField = new TextField("content", "", Field.Store.YES);
        String[] contents = {
                "Humpty Dumpty sat on a wall",
                "All the king's horses and all  the king's men",
                "Couldn't put Humpty together again."
        };
        for (String content : contents) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexWriter.addDocument(doc);
        }

        searcherManager.maybeRefresh();
        IndexSearcher indexSearcher = searcherManager.acquire();
        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse("humpty dumpty");
        TopDocs topDocs = indexSearcher.search(query, 100);

        searcherManager.release(indexSearcher);
        indexWriter.commit();

        //add more document to index here
        String[] contents2 = {
                "humpty dumpty kpj",
                "Last humpty ."
        };
        for (String content : contents2) {
            textField.setStringValue(content);
            doc.removeField("content");
            doc.add(textField);
            indexWriter.addDocument(doc);
        }
        QueryParser queryParser2 = new QueryParser("content", analyzer);
        Query query2 = queryParser2.parse("humpty");

        //This is important
        searcherManager.maybeRefresh();
        indexSearcher = searcherManager.acquire();
        TopDocs topDocs2 = indexSearcher.search(query2, 100);
        for (ScoreDoc scoreDoc : topDocs2.scoreDocs) {
            doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(scoreDoc.score + ": " + doc.getField("content").stringValue());
        }
        searcherManager.release(indexSearcher);
        indexWriter.commit();
    }
}
