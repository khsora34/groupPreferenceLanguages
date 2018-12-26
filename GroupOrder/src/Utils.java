public class Utils {

    public Utils() {
    }

    public static Language getLanguage(String shortValue) {
        Language language = null;

        Language[] availableLanguages = Language.values();

        for (int i = 0; language == null && i < availableLanguages.length; i++) {
            if (availableLanguages[i].getShortValue().compareTo(shortValue) == 0) {
                language = availableLanguages[i];
            }
        }

        return language;
    }
}
