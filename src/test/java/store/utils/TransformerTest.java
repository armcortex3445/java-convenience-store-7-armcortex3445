package store.utils;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

public class TransformerTest {


    @Test
    void testJoinToString(){
        assertThat(Transformer.joinToString(List.of(1,2,3,4,5),", ")).isEqualTo("1, 2, 3, 4, 5");
        assertThat(Transformer.joinToString(List.of(1,2,3,4,5),"-")).isEqualTo("1-2-3-4-5");
    }

    @Test
    void testParsePositiveInt(){
        assertThat(Transformer.parsePositiveInt("1000")).isEqualTo(1000);
        assertThatThrownBy(()->Transformer.parsePositiveInt("?")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()->Transformer.parsePositiveInt("")).isInstanceOf(IllegalArgumentException.class);
    }

}
