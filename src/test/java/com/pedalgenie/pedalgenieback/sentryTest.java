package com.pedalgenie.pedalgenieback;



import java.lang.Exception;
import io.sentry.Sentry;
import org.junit.jupiter.api.Test;


public class sentryTest {

        @Test
        void testSentryExceptionCapture() {
        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }
}
