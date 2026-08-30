package it.unicam.cs.mpgc.rpg130077;

import it.unicam.cs.mpgc.rpg130077.controller.UI.JavaFXTestHelper;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @BeforeAll
    public static void initJavaFX() {
        JavaFXTestHelper.initPlatform();
    }

    @Test
    @DisplayName("App extends javafx.application.Application")
    public void testAppClassHierarchy() {
        assertTrue(Application.class.isAssignableFrom(App.class),
                "App class must extend javafx.application.Application");
    }

    @Test
    @DisplayName("App start and stop lifecycle initializes Stage and cleans up resources")
    public void testAppStartAndStopLifecycle() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            App app = new App();
            Stage stage = new Stage();
            try {
                app.start(stage);

                assertNotNull(stage.getScene(), "Stage should have a Scene set after start()");
                assertNotNull(stage.getScene().getRoot(), "Scene should have a root Node");
            } finally {
                app.stop();
                stage.close();
            }
        });
    }

    @Test
    @DisplayName("main method is present, public, and static")
    public void testMainMethodSignature() throws NoSuchMethodException {
        Method mainMethod = App.class.getMethod("main", String[].class);
        assertNotNull(mainMethod, "App must define public static void main(String[] args)");
        int modifiers = mainMethod.getModifiers();
        assertTrue(Modifier.isPublic(modifiers), "main method must be public");
        assertTrue(Modifier.isStatic(modifiers), "main method must be static");
        assertEquals(void.class, mainMethod.getReturnType(), "main method must return void");
    }
}
