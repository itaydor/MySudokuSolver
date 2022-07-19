package v2;

public class IllegalValueException extends Throwable {

    public IllegalValueException(int length) {
        super(length + " doesn't have integer square root.");
    }
}
