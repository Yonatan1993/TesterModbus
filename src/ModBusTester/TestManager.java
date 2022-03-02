package ModBusTester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestManager {


    public static String getTime() {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);

    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";


    public TestManager() {
    }

    public void relayOutputTest(ModbusHandler modbusHandler) throws InterruptedException,
            TesterExceptions {
        File csvFile = new File("relay Short Test.csv");
        try (PrintWriter out = new PrintWriter(csvFile)) {
            out.printf("%s,%s,%s,%s\n", "Relaynumber ", "Short Relay Test", "Open Relay Test", "Time");

            for (int i = 0; i < 4; i++) {
                AtomicBoolean testRelayStatusOk = new AtomicBoolean();//Why we need atomic?
                int finalI = i;
                modbusHandler.relayShort((byte) i, 50, new IResultListener() {
                    @Override
                    public void RelayOnResult(int status, TesterExceptions ex) {
                        System.out.println("First status received: " + status);
                        if (status == 0) {
                            testRelayStatusOk.set(true);
                            out.printf("\t%d,\t%d,\t%s,\t%s,\n", finalI, 0, "NA", TestManager.getTime());
                        } else {
                            System.out.println(ex.getMessage());
                            out.printf("\t%d,\t%d,\t%s,\t%s,\n", finalI, 1, "NA", TestManager.getTime());
                            System.out.println("Dtam"); //TODO: DELETE THIS
                        }
                    }

                    @Override
                    public void RelayOffResult(int status, TesterExceptions ex) {
                        System.out.println("Second status received: " + status);
                        if (status == 0) {
                            testRelayStatusOk.set(true);
                            out.printf("\t%d,\t%s,\t%d,\t%s,\n", finalI, "NA", 0, TestManager.getTime());
                        } else {
                            System.out.println(ex.getMessage());
                            out.printf("\t%d,\t%s,\t%d,\t%s,\n", finalI, "NA", 1, TestManager.getTime());
                        }
                    }
                });

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Com.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (!testRelayStatusOk.get()) {
                    System.err.println(ANSI_RED + "Failed to setup relay number " + (i+1) + ANSI_RESET);
                } else {
                    System.out.println(ANSI_BLUE + "Successfully setup relay number " + (i+1) + ANSI_RESET);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + "------> Check if The File not already open, If yes, close in and run the test again");
        }
    }


    public void relayOutputAndInputTest(ModbusHandler modbusHandler) throws InterruptedException, TimeoutException,
            TooManyListenersException {
        File csvFile = new File("relay IO Test.csv");
        try (PrintWriter out = new PrintWriter(csvFile)) {
            out.printf("%s,%s,%s,%s,%s,%s,\n", "Relay number ", "initial state CLOSE", " Relay Test OPEN", "Input Test", "end state CLOSE", "Time");


            boolean input1State;
            for (int i = 0; i < 4; i++) {

                modbusHandler.cleanAllMessagesInQueue();
                switchRelayOff(modbusHandler, (byte) i, out);

                modbusHandler.cleanAllMessagesInQueue();
                switchRelayOn(modbusHandler, (byte) i, out);


                int inputsState = 0;
                try {
                    inputsState = modbusHandler.readInputs();
                } catch (TesterExceptions e) {//Unable to listen to modbus
                    System.out.println(e.getMessage());
                }
                System.out.println("inputsState: " + inputsState);
                System.out.println("Cheaker: " + (1 << (4 - i - 1)));
                input1State = ((inputsState == (1 << (4 - i - 1))));
                if (input1State) {
                    System.out.println(ANSI_BLUE + "Realay and input number " + (i + 1) + " is valid" + ANSI_RESET);
                    out.printf("%d,", 0);
                } else {
                    System.out.println(ANSI_RED + "Input Output Error in Relay number " + (i + 1) + ANSI_RESET);
                    out.printf("%d,", 1);
                }
                System.out.println("*******************************************");

                modbusHandler.setRelayState(false, (byte) i, new IResultListener() {
                    @Override
                    public void RelayOnResult(int status, TesterExceptions ex) {
                        //not relevat the relay iS OFF
                    }

                    @Override
                    public void RelayOffResult(int status, TesterExceptions ex) {
                        if (status == 0) {
                            out.printf("%d,%s,\n", 0, TestManager.getTime());
                        } else {
                            System.out.println(ex.getMessage());
                            out.printf("%d,%s,\n", 1, TestManager.getTime());
                        }
                    }
                }); //TODO: make sure the relay is off
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + "------> Check if The File not already open, If yes, close in and run the test again");
        }
    }

    public void speedTest(ModbusHandler modbusHandler) {

        File csvFile = new File("Speed Test.csv");
        try (PrintWriter out = new PrintWriter(csvFile)) {
            out.printf("%s,%s,%s,%s\n", "Relay number ", " initial Short Relay Test", "Open Relay Test","Interval Time in millis","result","Final Short Relay Test", "Time");

            for (int i = 0; i < 4; i++) {
                AtomicBoolean testRelayStatusOk = new AtomicBoolean();//Why we need atomic?
                int finalI = i;
                modbusHandler.relayShort((byte) i, 1000, new IResultListener() {
                    @Override
                    public void RelayOnResult(int status, TesterExceptions ex) {
                        System.out.println("First status received: " + status);
                        if (status == 0) {
                            testRelayStatusOk.set(true);
                            out.printf("\t%d,\t%d,\t%s,\t%s,\n", finalI, 0, "NA", TestManager.getTime());
                        } else {
                            System.out.println(ex.getMessage());
                            out.printf("\t%d,\t%d,\t%s,\t%s,\n", finalI, 1, "NA", TestManager.getTime());
                        }
                    }

                    @Override
                    public void RelayOffResult(int status, TesterExceptions ex) {
                        System.out.println("Second status received: " + status);
                        if (status == 0) {
                            testRelayStatusOk.set(true);
                            out.printf("\t%d,\t%s,\t%d,\t%s,\n", finalI, "NA", 0, TestManager.getTime());
                        } else {
                            System.out.println(ex.getMessage());
                            out.printf("\t%d,\t%s,\t%d,\t%s,\n", finalI, "NA", 1, TestManager.getTime());
                        }
                    }
                });

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Com.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (!testRelayStatusOk.get()) {
                    System.err.println(ANSI_RED + "Failed to setup relay number " + i + ANSI_RESET);
                } else {
                    System.out.println(ANSI_BLUE + "Successfully setup relay number " + i + ANSI_RESET);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + "------> Check if The File not already open, If yes, close in and run the test again");
        }
    }


    private void switchRelayOn(ModbusHandler modbusHandler, byte i, PrintWriter out) throws TooManyListenersException, InterruptedException, TimeoutException {

        modbusHandler.setRelayState(true, (byte) i, new IResultListener() {
            @Override
            public void RelayOnResult(int status, TesterExceptions ex) {
                System.out.println("First status received: " + status);
                if (status == 0) {
                    out.printf("%d,", 0);
                } else {
                    System.out.println(ex.getMessage());
                    out.printf("%d,", 1);
                }
            }

            @Override
            public void RelayOffResult(int status, TesterExceptions ex) {
                //not relevant, the relay is on here
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Com.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void switchRelayOff(ModbusHandler modbusHandler, byte i, PrintWriter out) throws TooManyListenersException, InterruptedException, TimeoutException {
        modbusHandler.setRelayState(false, (byte) i, new IResultListener() {
            @Override
            public void RelayOnResult(int status, TesterExceptions ex) {
                // not relevant the relay is off here
            }

            @Override
            public void RelayOffResult(int status, TesterExceptions ex) {
                System.out.println("Second status received: " + status);
                if (status == 0) {
                    out.printf("\t%d,\t%d,", i, 0);
                } else {
                    System.out.println(ex.getMessage());
                    out.printf("\t%d,\t%d,", i, 1);
                }
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Com.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

