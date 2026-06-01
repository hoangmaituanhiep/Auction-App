package app.payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Arrays;

public class OnlineUserResponseTest {

    @Test
    void getOnlineUsernames_returnsProvidedList() {
        List<String> users = Arrays.asList("a", "b");
        OnlineUserResponse r = new OnlineUserResponse(users);

        assertEquals(users, r.getOnlineUsernames());
    }
}
