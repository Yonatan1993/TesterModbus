package ModBusTester;

public interface IResultListener {


        void RelayOnResult(int status, TesterExceptions ex);
        void RelayOffResult(int status, TesterExceptions ex);
    }

