package vitalii.haidarenko.test.entities;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import org.w3c.dom.Node;

/**
 * Entity, which contain the same attr as original element
 *
 * @author vitalii.haidarenko (vhaidare)
 * @since 0.1
 */
@Data
@Builder(toBuilder = true)
public class CompareEntity {

    private Node node;
    private Map<String, String> sameAttrMap;

    public void addAttribute(String key, String value) {
        if (getSameAttrMap() == null) {
            sameAttrMap = new HashMap<>();
        }
        getSameAttrMap().put(key, value);
    }
}