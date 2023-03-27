package spikes.c5_nrt;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class NRT {
    public static void main(String[] args) throws IOException, ParseException {
        Directory directory = FSDirectory.open(new File("data/index").toPath());
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

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


        //Here, we are not passing a directory. Instead we pass in an IndexWriter to provide a coherent index. The 2nd argument is a boolean value
        // that controls whether to apply all the outstanding deletes. This way also we have achieved real time search
        DirectoryReader directoryReader = DirectoryReader.open(indexWriter, true);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse("humpty dumpty");

        TopDocs topDocs = indexSearcher.search(query, 100);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(scoreDoc.score + ": " + doc.getField("content").stringValue());
        }
        indexWriter.commit();

    }
}
