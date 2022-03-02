package ModBusTester;

public class TesterExceptions extends Exception {


    private final ErrorCodeEnum errorCode;

    public TesterExceptions(ErrorCodeEnum errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
