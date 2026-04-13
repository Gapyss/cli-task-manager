package domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SmokeTest {
    @Test
    void junit_is_wired() {
        assertThat(1 + 1).isEqualTo(2);
    }
}
