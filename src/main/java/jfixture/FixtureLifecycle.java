package jfixture;

public class FixtureLifecycle {
    private final FixtureDefinition definition;
    private boolean isSetUp = false;
    private Object object;

    public FixtureLifecycle(FixtureDefinition definition) {
        this.definition = definition;
    }

    public Object setUp() {
        if (!isSetUp) {
            object = definition.setUp();
            isSetUp = true;
        }
        return object;
    }

    public void tearDown() {
        if (!isSetUp) {
            throw new IllegalStateException("not set up");
        }
        definition.tearDown(object);
        object = null;
        isSetUp = false;
    }
}
