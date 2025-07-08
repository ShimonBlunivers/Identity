package me.blunivers.identity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Utility {
	public static String componentToString(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}
}
