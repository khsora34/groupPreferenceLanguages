public enum Language {
    ENGLISH ("en"),
    SPANISH ("sp"),
    GERMAN ("ger"),
    POLISH ("pol"),
    UKRANIAN ("ukr"),
    CROATIAN ("cro"),
    FRENCH ("fr"),
    LATIN ("lat"),
    PORTUGUESE ("port"),
    RUSSIAN ("russ");

    private final String shortValue;

    Language (String shortValue) {
        this.shortValue = shortValue;
    }

    public String getShortValue() {
        return shortValue;
    }

    @Override
    public String toString() {
        return super.toString() + " -> " + this.getShortValue();
    }
}
