package Laden;

import client.UDPSocketClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import pojo.Item;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class LadenTestCases {
    Laden laden;

    @BeforeEach
    public void setup() {
        laden = Mockito.mock(Laden.class);

        doCallRealMethod().when(laden).parseRemoveRequest(Mockito.any());
        doCallRealMethod().when(laden).setLogs(Mockito.any());
        doCallRealMethod().when(laden).setInventory(Mockito.any());
        doCallRealMethod().when(laden).getInventory();

        doNothing().when(laden).addToHistory(Mockito.any(), Mockito.any());
    }

    @Test
    public void whenParseRemoveRequestWithAvailableItem_shouldReturnSuccessLog(){
        laden.setLogs(new ArrayList<>());
        List<Item> testlist = new ArrayList<>();
        testlist.add(new Item("chair"));
        laden.setInventory(testlist);
        String actual = laden.parseRemoveRequest(new String[]{"","chair", "abcd"});
        Assertions.assertTrue(actual.contains("Removing item: "));

    }
    @Test
    public void whenParseRemoveRequestWithUnavailableItem_shouldReturnFailureLog(){
        laden.setLogs(new ArrayList<>());
        List<Item> testlist = new ArrayList<>();
        testlist.add(new Item("chair"));
        laden.setInventory(testlist);
        String actual = laden.parseRemoveRequest(new String[]{"","laptop", "abcd"});
        Assertions.assertTrue(actual.contains("Tried to remove item "));

    }
}
