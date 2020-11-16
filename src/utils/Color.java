package utils;

public enum Color {

    RED ("\u001B[31m"),
    BLACK ("\u001B[30m"),
    GREEN ("\u001B[32m"),
    BLUE ("\u001B[34m"),
    PURPLE ("\u001B[35m"),
    CYAN ("\u001B[36m"),
    WHITE ("\u001B[37m"),
    RESET ("\u001B[0m");

    String value;

    Color(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
