package jfixture;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FixtureManagerTest {

    private static class MyFixtureDefinition extends FixtureDefinition {
        public MyFixtureDefinition(Class<?> type) {
            super(type);
        }

        @Override
        public Object setUp(FixtureResolver resolver) {
            return null;
        }

        @Override
        public void tearDown(Object object) {
        }
    }

    @Test
    public void choose_last_definition_by_type() {
        var stringDefinition = new MyFixtureDefinition(String.class);
        var listDefinition = new MyFixtureDefinition(List.class);
        var manager = new FixtureManager(Arrays.asList(stringDefinition, listDefinition));

        var definition = manager.findFixtureDefinition(String.class);
        assertSame(stringDefinition, definition);

        definition = manager.findFixtureDefinition(List.class);
        assertSame(listDefinition, definition);
    }

    @Test
    public void choose_sub_type_if_possible() {
        var listDefinition = new MyFixtureDefinition(List.class);
        var arrayListDefinition = new MyFixtureDefinition(ArrayList.class);
        var manager = new FixtureManager(Arrays.asList(listDefinition, arrayListDefinition));

        var definition = manager.findFixtureDefinition(List.class);
        assertSame(arrayListDefinition, definition);
    }

    @Test
    public void when_no_definition_chosen_then_exception_will_be_thrown() {
        var manager = new FixtureManager(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> manager.getFixtureLifecycle(String.class));
    }

    @Test
    public void lifecycle_is_reused_for_same_requested_type() {
        var listDefinition = new MyFixtureDefinition(List.class);
        var manager = new FixtureManager(Collections.singletonList(listDefinition));

        var lifecycle1 = manager.getFixtureLifecycle(List.class);
        var lifecycle2 = manager.getFixtureLifecycle(List.class);
        assertSame(lifecycle1, lifecycle2);
    }

    @Test
    public void lifecycle_is_reused_for_same_definition_type() {
        var arrayListDefinition = new MyFixtureDefinition(ArrayList.class);
        var manager = new FixtureManager(Collections.singletonList(arrayListDefinition));

        var lifecycle1 = manager.getFixtureLifecycle(ArrayList.class);
        var lifecycle2 = manager.getFixtureLifecycle(List.class);
        assertSame(lifecycle1, lifecycle2);
    }

    @Test
    public void set_up_type() {
        var stringDefinition = new MyFixtureDefinition(String.class);
        var manager = new FixtureManager(Collections.singletonList(stringDefinition));

        assertFalse(manager.getFixtureLifecycle(String.class).isSetUp());
        manager.setUp(String.class);
        assertTrue(manager.getFixtureLifecycle(String.class).isSetUp());
    }

    @Test
    public void tear_down_all() {
        var stringDefinition = new MyFixtureDefinition(String.class);
        var manager = new FixtureManager(Collections.singletonList(stringDefinition));

        manager.setUp(String.class);
        assertTrue(manager.getFixtureLifecycle(String.class).isSetUp());
        manager.tearDown();
        assertFalse(manager.getFixtureLifecycle(String.class).isSetUp());
    }

}
