package io.github.maki99999.biomebeats.util;

import net.minecraft.resources.ResourceLocation;

public class StringUtils {
    public static String formatToTitleCase(String str) {
        StringBuilder titleCase = new StringBuilder();
        boolean capitalizeNext = true;
        char[] charArray = str.trim().toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            if (i > 0 && (Character.isUpperCase(charArray[i]) && (Character.isLowerCase(charArray[i - 1]))
                    || (Character.isLetter(charArray[i]) && Character.isDigit(charArray[i - 1]))
                    || (Character.isDigit(charArray[i]) && Character.isLetter(charArray[i - 1])))) {
                titleCase.append(" ").append(charArray[i]);
            } else if (Character.isWhitespace(charArray[i])
                    || charArray[i] == '_' || charArray[i] == '-' || charArray[i] == '/') {
                capitalizeNext = true;
                if (i == 0 || !(Character.isWhitespace(charArray[i - 1]) || charArray[i - 1] == '_'
                        || charArray[i - 1] == '-')) {
                    titleCase.append(" ");
                    if (charArray[i] == '/') {
                        titleCase.append("/ ");
                    }
                }
            } else if (capitalizeNext) {
                titleCase.append(Character.toUpperCase(charArray[i]));
                capitalizeNext = false;
            } else {
                titleCase.append(Character.toLowerCase(charArray[i]));
            }
        }

        return titleCase.toString();
    }

    public static String formatToTitleCase(ResourceLocation resourceLocation, boolean keepPath) {
        String path = resourceLocation.getPath();
        return formatToTitleCase(path.substring((keepPath ? path.indexOf('/') : path.lastIndexOf('/')) + 1));
    }

    public static String formatToTitleCase(ResourceLocation resourceLocation) {
        return formatToTitleCase(resourceLocation, false);
    }
}
