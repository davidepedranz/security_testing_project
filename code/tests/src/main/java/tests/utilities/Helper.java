package tests.utilities;

import net.sourceforge.jwebunit.junit.WebTester;

/**
 * Helper class to perform common operations in the testing of SchoolMate,
 * for example the login operation.
 *
 * @author Davide Pedranz
 */
public final class Helper {

    // constants
    public static final String BASE_URL = "http://localhost:8000/";

    // dependencies
    private final WebTester tester;

    /**
     * Construct a new instance of the Helper.
     *
     * @param tester An instance of WebTester pointing to the vulnerable application.
     */
    public Helper(WebTester tester) {
        this.tester = tester;
    }

    /**
     * Perform a login from the home page.
     *
     * @param username Username.
     * @param password Password.
     */
    public void login(String username, String password) {
        tester.beginAt("index.php");
        tester.setTextField("username", username);
        tester.setTextField("password", password);
        tester.submit();
    }

    public void loginAsAdmin() {
        login("test", "test");
    }

    public void loginAsTeacher() {
        login("teacher", "teacher");
    }
}
