package vitalii.haidarenko.test;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import vitalii.haidarenko.test.utility.XmlDocumentHelper;

/**
 * Main class, whioch run all application
 *
 * @author vitalii.haidarenko (vhaidare)
 * @since 0.1
 */
public class Main {

    private final static String ELEMENT_ID = "make-everything-ok-button";

    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception("Should be 2 required params:\n1) Path to original file;\n2)Path to modify file");
        }

        try {
            final Path originalPath = Path.of(args[0]);
            final Path modifyPath = Path.of(args[1]);

            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final Document originalDocument = builder.parse(originalPath.toFile());
            final Document modifyDocument = builder.parse(modifyPath.toFile());

            final XmlDocumentHelper parser = new XmlDocumentHelper();
            final String result = parser.foundSimilarButton(originalDocument, ELEMENT_ID, modifyDocument);
            System.out.println("Element found by path: " + result);
        } catch (final InvalidPathException invalidPath) {
            System.out.println("Can not load file. Exception: " + invalidPath);
        }
    }

}
