package vitalii.haidarenko.test.utility;

import static java.text.MessageFormat.format;
import static javax.xml.xpath.XPathConstants.NODE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.dom.DeferredAttrImpl;
import org.apache.xerces.dom.DeferredElementImpl;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import vitalii.haidarenko.test.entities.CompareEntity;

/**
 * Class, which help to work with {@link Document}
 *
 * @author vitalii.haidarenko (vhaidre)
 * @since 0.1
 */
public class XmlDocumentHelper {

    private final static String FIND_BUTTON_EXPRESSION = ".//*[@id=\u0027\u0027{0}\u0027\u0027]";
    private final static String FIND_ALL_BUTTONS = ".//*[@class=starts-with('btn','')]";
    private final static String NODE_VALUE = "node_value";
    private final static String SEPARATOR = ">";

    private final XPath xPath = XPathFactory.newInstance().newXPath();
    private final Map<String, String> mainElementAttrMap = new HashMap<>();
    private final StringBuilder builder = new StringBuilder();

    /**
     * Found in {@param originalDocument} element with special id and try to fount the same or the
     * most similar element in other document
     *
     * @param originalDocument - originalDocument document, from where element with id = {@param elementId}
     *                         will be found
     * @param elementId        - element, which will be founded in {@param originalDocument}
     * @param modify           - document, where will be found similar button
     */
    public String foundSimilarButton(final Document originalDocument,
                                     final String elementId,
                                     final Document modify) {
        try {
            final DeferredElementImpl mainButton =
                    (DeferredElementImpl) xPath.compile(format(FIND_BUTTON_EXPRESSION, elementId))
                                               .evaluate(originalDocument, NODE);
            fillAttributeMap(mainElementAttrMap, mainButton);
            final Node foundNode = foundSimilarNode(modify);

            return getNodePath(foundNode);
        } catch (final Exception ex) {
            return "Error while founding button. Exception message:" + ex.getMessage();
        }
    }

    private String getNodePath(final Node node) {
        builder.setLength(0);

        Node parentNode = node;
        while (parentNode.getParentNode() != null) {
            builder.insert(0, parentNode.getNodeName()).insert(0, SEPARATOR);
            parentNode = parentNode.getParentNode();
        }
        return builder.deleteCharAt(0).toString();
    }

    private void fillAttributeMap(final Map<String, String> map, final DeferredElementImpl element) {
        final var attributes = element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            final Node nodeItem = attributes.item(i);
            map.put(((DeferredAttrImpl) nodeItem).getName(), nodeItem.getNodeValue());
        }
        map.put(NODE_VALUE, element.getFirstChild().getNodeValue().trim());
    }

    private Node foundSimilarNode(final Document doc) {
        final Map<Integer, Node> nodeMap = new HashMap<>();
        try {
            final var buttonList = ((DTMNodeList) xPath.compile(FIND_ALL_BUTTONS)
                                                       .evaluate(doc, XPathConstants.NODESET));

            for (int i = 0; i < buttonList.getLength(); i++) {
                final Node node = buttonList.item(i);
                final var attributes = node.getAttributes();

                mainElementAttrMap.forEach((s, s2) -> {
                    try {
                        final String attrValue = attributes.getNamedItem(s).getNodeValue();
                        if (attrValue.equals(s2)) {
                            nodeMap.put(((DeferredElementImpl) node).getNodeIndex(), node);
                        }
                    } catch (final Exception ex) {
                        // ignore
                    }
                });
            }

            if (nodeMap.size() > 1) {
                final List<CompareEntity> compare = new ArrayList<>();

                nodeMap.forEach((integer, node) -> {
                    final Map<String, String> fountAttrMap = new HashMap<>();
                    final CompareEntity entity = CompareEntity.builder()
                                                              .node(node)
                                                              .build();

                    fillAttributeMap(fountAttrMap, (DeferredElementImpl) node);
                    fountAttrMap.forEach((key, value) -> {
                        if (mainElementAttrMap.get(key) != null && mainElementAttrMap.get(key).equals(value)) {
                            entity.addAttribute(key, value);
                        }
                    });
                    compare.add(entity);
                });

                System.out.println("Original node: " + mainElementAttrMap.toString());
                System.out.println("Found node with the most similar attributes");

                final CompareEntity compareEntity = compare.stream()
                                         .peek(entity -> {
                                             System.out.println("Node:");
                                             System.out.println(entity.toString());
                                         })
                                         .max(Comparator.comparingInt(o -> o.getSameAttrMap().size()))
                                         .get();
                System.out.println("The most similar node with origin: " + compareEntity);
                return compareEntity.getNode();
            } else {
                return nodeMap.get(0);
            }
        } catch (final Exception e) {
            return null;
        }
    }

}
