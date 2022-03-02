package ModBusTester;

public enum  ErrorCodeEnum {

    RELAY_CLOSE_CIRCUIT(1,"Unable to short the relay"),
    RELAY_OPEN_CIRCUIT(2,"Unable to open the relay"),
    MODBUS_NOT_LISTENING(3,"Modbus board not responsing ");


    public int getInternalErrorCode() {
        return internalErrorCode;
    }

    public  String getDescription() {
        return description;
    }


    private final int internalErrorCode;
    private final  String description;
    ErrorCodeEnum(int internalErrorCode, String description)
    {
        this.internalErrorCode=internalErrorCode;
        this.description=description;
    }
}
