package sensor;

import client.UDPSocketClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import pojo.Item;
import pojo.ItemList;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class SensorTestCases {

    Sensor sensor = new Sensor();
    UDPSocketClient mockUDP;

    @BeforeEach
    public void setup() {
        mockUDP = Mockito.mock(UDPSocketClient.class);
        doNothing().when(mockUDP).sendMsgCustomDefault(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void whenGenerateRandomItem_shouldBelongsInItemList(){
        Item sampleItem = sensor.generateItem();
        Assertions.assertTrue(ItemList.list.contains(sampleItem.getName()));
    }

    @Test
    public void whenSensorRemoveItem_shouldWorks() throws IOException {
        sensor.setIn(false);
        sensor.setUdpSocket(mockUDP);
        String removeLog = sensor.scanItem("sample");
        Assertions.assertTrue(removeLog.contains("Removing item: "));
    }

    @Test
    public void whenSensorScanItem_shouldWorks() throws IOException {
        sensor.setIn(true);
        sensor.setUdpSocket(mockUDP);
        String removeLog = sensor.scanItem("sample");
        Assertions.assertTrue(removeLog.contains("Scanning item : "));
    }
}
