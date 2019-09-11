package vitalii.haidarenko.test;

import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import vitalii.haidarenko.test.finder.DocumentParser;

/**
 * TODO: Change class description
 *
 * @author vitalii.haidarenko (<NetworkID>)
 * @since 0.11
 */
public class Main {


    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception("Should be 2 required params:\n1) Path to original file;\n2)Path to modify file");
        }

        final Path originalPath = Path.of(args[0]);
        final Path modifyPath = Path.of(args[1]);

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        Document originalDocument = builder.parse(originalPath.toFile());
        Document modifyDocument = builder.parse(modifyPath.toFile());

        final DocumentParser parser = new DocumentParser();
        parser.compareDocuments(originalDocument, modifyDocument);
    }

}
