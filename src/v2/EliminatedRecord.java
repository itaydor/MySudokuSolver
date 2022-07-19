package v2;

public record EliminatedRecord(String method, Cell cell, int eliminatedValue) {

    @Override
    public String toString() {
        return "Method=" + method +
                ", EliminatedValue=" + eliminatedValue +
                ", Cell=" + cell;
    }
}
