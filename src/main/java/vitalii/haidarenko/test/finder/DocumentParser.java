package vitalii.haidarenko.test.finder;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.dom.DeferredAttrImpl;
import org.apache.xerces.dom.DeferredElementImpl;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * TODO: Change class description
 *
 * @author vitalii.haidarenko (<NetworkID>)
 * @since 0.11
 */
public class DocumentParser {

    private final static String FIND_BUTTON_EXPRESSION = ".//*[@id='make-everything-ok-button']";
    private final static String FIND_BY_EXPRESSION = ".//*[@{0}=\u0027\u0027{1}\u0027\u0027]";
    private final static String HREF = "href";
    private final static String ON_CLICK = "onclick";
    private final static String NODE_VALUE = "node_value";
    private final XPath xPath = XPathFactory.newInstance().newXPath();
    final Map<String, String> attributeMap = new HashMap<>();

    public void compareDocuments(final Document original, final Document modify) throws XPathExpressionException {
        final DeferredElementImpl node =
                (DeferredElementImpl) xPath.compile(FIND_BUTTON_EXPRESSION).evaluate(original, XPathConstants.NODE);

        final NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            final Node nodeItem = attributes.item(i);
            attributeMap.put(((DeferredAttrImpl) nodeItem).getName(), nodeItem.getNodeValue());
        }
        attributeMap.put(NODE_VALUE, node.getFirstChild().getNodeValue().trim());
        final Node element = getElementBySubClass(modify);
        Node parentNode = element;
        StringBuilder builder = new StringBuilder();
        while (parentNode.getParentNode() != null) {
            builder.insert(0, parentNode.getNodeName()).insert(0, '/');
            parentNode = parentNode.getParentNode();
        }

        System.out.println("Element found by path: " + builder.toString());
    }

    private Node getElementBySubClass(final Document doc) {
        try {
            final DTMNodeList buttonList = ((DTMNodeList) xPath.compile(".//*[@class=starts-with('btn','')]")
                                                               .evaluate(doc, XPathConstants.NODESET));
            final Map<Integer, Node> nodeMap = new HashMap<>();
            for (int i = 0; i < buttonList.getLength(); i++) {
                final Node node = buttonList.item(i);
                final var attributes = node.getAttributes();


                attributeMap.forEach((s, s2) -> {
                    try {
                        final String attrValue = attributes.getNamedItem(s).getNodeValue();
                        final var onClickAttr = attributes.getNamedItem(ON_CLICK);
                        final var hrefAttr = attributes.getNamedItem(HREF);
                        if (onClickAttr != null && attrValue.equals(s2)) {
                            nodeMap.put(((DeferredElementImpl) node).getNodeIndex(), node);
                        }
                    } catch (final Exception ex) {
                        // ignore
                    }

                });


            }

            if (nodeMap.size() > 1) {
                return nodeMap.entrySet().stream()
                              .filter(entry -> {
                                  final var child = entry.getValue().getFirstChild();
                                  return child != null &&
                                          child.getNodeValue().trim().equals(attributeMap.get(NODE_VALUE));
                              })
                              .map(Map.Entry::getValue)
                              .findFirst()
                              .orElse(null);
            } else {
                return nodeMap.get(0);
            }
        } catch (
                final Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private String getExpression(final String param, final String value) {
        return MessageFormat.format(FIND_BY_EXPRESSION, param, value);
    }

}
