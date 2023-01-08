import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HLogTest {

    @Test
    void hInfo() {
        HLog.hInfo("Test HLog for build integer: $i, decimal: $d, string: $s, boolean: $b", 1, 1.313, "h-log test", true);
    }
}