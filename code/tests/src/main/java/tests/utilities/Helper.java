package tests.utilities;

import net.sourceforge.jwebunit.junit.WebTester;

import javax.servlet.http.Cookie;

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
    @SuppressWarnings("WeakerAccess")
    public void login(String username, String password) {
        tester.beginAt("index.php");
        tester.setTextField("username", username);
        tester.setTextField("password", password);
        tester.submit();
    }

    /**
     * Login as the Admin test user.
     */
    public void loginAsAdmin() {
        login("test", "test");
    }

    /**
     * Login as the Teacher test user.
     */
    public void loginAsTeacher() {
        login("teacher", "teacher");
    }

    /**
     * Login as the Student test user.
     */
    public void loginAsStudent() {
        login("student", "student");
    }

    /**
     * Get the current PHP session.
     *
     * @return Representation of the cookie for the session, in the form "name=value".
     */
    @SuppressWarnings("SpellCheckingInspection")
    public String getSessionCookie() {
        final Cookie cookie = (Cookie) tester.getTestingEngine().getCookies().get(0);
        assert "PHPSESSID".equals(cookie.getName()) : "It was not possible to extract the PHP session from the current page";
        return cookie.getName() + "=" + cookie.getValue();
    }
}
