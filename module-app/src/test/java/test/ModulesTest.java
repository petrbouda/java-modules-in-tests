package test;

import org.junit.Test;
import pbouda.modules.TestComponent;

public class ModulesTest {

    @Test
    public void accessibility() {
        System.out.println(TestComponent.class.getSimpleName());
    }
}
