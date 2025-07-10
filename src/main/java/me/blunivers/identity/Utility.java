package me.blunivers.identity;

import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Utility {
	public static String componentToString(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	public static Map<String, String> metadataToMap(String metadata) {
		Map<String, String> map = new HashMap<>();
		for (String value : metadata.split(",")) {
			String[] parts = value.split(":");
			map.put(parts[0], parts[1]);
		}
		return map;
	}

	public static String metadataSerialize(Map<String, String> metadataMap) {
		String metadata = "";
		for (String key : metadataMap.keySet()) {
			if (!metadata.isEmpty()) {
				metadata += ",";
			}
			metadata += key + ":" + metadataMap.get(key);
		}
		return metadata;
	}
}
