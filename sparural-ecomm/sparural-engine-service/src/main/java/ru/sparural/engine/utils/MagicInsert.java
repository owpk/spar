package ru.sparural.engine.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MagicInsert {

    /**
     * According to the pattern, we get the keys from {{key}}.
     * If the key contains the format {{key.key.key}} ->
     * split the value by points and get to the last one.
     * If, when selecting the last value, json == null -> there are several values of this key
     * Update description with use replace
     */

    public static String convert(ObjectNode objectNode, String description) {
        String r = "\\{\\{(.*?)}}+";

        List<String> listWithValue = new ArrayList<>();
        List<String> listWithKeyOfChild;
        List<String> listWithKey = new ArrayList<>();
        String desc = "";
        Pattern pattern = Pattern.compile(r);
        Matcher matcher = pattern.matcher(description);
        while (matcher.find()) {
            listWithKey.add(matcher.group(1));
        }
        JsonNode json = objectNode;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(String.valueOf(objectNode));
            Map<String, String> map = new HashMap<>();
            addKeys("", root, map, new ArrayList<>());
            Map<String, String> baba = addKeys("", root, map, new ArrayList<>());

            for (String key : listWithKey) {
                if (baba.get(key) != null) {
                    desc = (baba.get(key));

                    description = description.replace("{{" + key + "}}", String.valueOf(desc));
                } else {
                    description = description.replace("{{" + key + "}}", "");
                }

            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return description;
    }

    public static Map<String, String> addKeys(String currentPath, JsonNode jsonNode, Map<String, String> map, List<Integer> suffix) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map, suffix);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;

            for (int i = 0; i < arrayNode.size(); i++) {
                suffix.add(i + 1);
                addKeys(currentPath, arrayNode.get(i), map, suffix);

                if (i + 1 < arrayNode.size()) {
                    try {
                        suffix.remove(arrayNode.size() - 1);
                    } catch (Exception e) {

                    }
                }
            }

        } else if (jsonNode.isValueNode()) {
            if (currentPath.contains("-")) {
                for (int i = 0; i < suffix.size(); i++) {
                    currentPath += "-" + suffix.get(i);
                }

                suffix = new ArrayList<>();
            }

            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, valueNode.asText());
        }
        return map;
    }

}
