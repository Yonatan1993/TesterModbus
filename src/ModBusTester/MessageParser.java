package ModBusTester;


import java.util.*;

public class MessageParser {

    public List<Byte> data;
    final int dataLength = 16;


    public MessageParser() {

        data = new ArrayList<>();
    }

    public Byte[] listToArray() {
        Byte[] rezData = new Byte[dataLength];
        data.toArray(rezData);
        return rezData;

    }


    public List<Byte> getData() {
        return data;
    }

    //TODO HERE I ASSUMING THAT THE LENGTH IS 6 bYTE. WHAT IF IT WILL BE LARGER?
    public int parseData(List<Byte> data) {

        this.data.addAll(data);
        if (data.size() < 6) {
            return -1;
        }
        finalParsing();
        return 0;
    }

    public class ModbusMessage {
        int address;
        int command;
        int byteCount;
        byte[] data;
        int crc;

        @Override
        public String toString() {
            StringBuilder str
                    = new StringBuilder();

            str.append(String.format("%02X ", address));
            str.append(String.format("%02X ", command));
            str.append(String.format("%02X ", byteCount));
            for (int i = 0; i < byteCount; i++)
                str.append(String.format("%02X ", data[i]));
            str.append(String.format("%02X ", crc));
            return str.toString();

        }
    }

    ModbusMessage message;

    private void finalParsing() {
        ModbusMessage modbusMessage = new ModbusMessage();
        Iterator<Byte> iter = data.iterator();
        modbusMessage.address = iter.next();
        modbusMessage.command = iter.next();
        modbusMessage.byteCount = iter.next();
        int lengthOfData = modbusMessage.byteCount;
        modbusMessage.data = new byte[lengthOfData];

        for (int idx = 0; idx < lengthOfData; idx++) {
            modbusMessage.data[idx] = iter.next();
        }
        modbusMessage.crc = ((iter.next() << 8) | iter.next());
        this.message = modbusMessage;
    }


    public ModbusMessage getMessage() {
        return this.message;
    }
}
