package tests.utilities;

import net.sourceforge.jwebunit.junit.WebTester;
import okhttp3.*;

import javax.servlet.http.Cookie;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

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
    private final OkHttpClient client;

    /**
     * Construct a new instance of the Helper.
     *
     * @param tester An instance of WebTester pointing to the vulnerable application.
     */
    public Helper(WebTester tester) {
        this.tester = tester;
        this.client = new OkHttpClient();
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
     * Login as the Parent test user.
     */
    public void loginAsParent() {
        login("parent", "parent");
    }

    /**
     * Get the current PHP session.
     *
     * @return Representation of the cookie for the session, in the form "name=value".
     */
    @SuppressWarnings({"WeakerAccess", "SpellCheckingInspection"})
    public String getSessionCookie() {
        final Cookie cookie = (Cookie) tester.getTestingEngine().getCookies().get(0);
        assert "PHPSESSID".equals(cookie.getName()) : "It was not possible to extract the PHP session from the current page";
        return cookie.getName() + "=" + cookie.getValue();
    }

    /**
     * Restore the initial status of the Student test user.
     */
    public void cleanupStudentTestUser() throws IOException {

        // login as admin
        loginAsAdmin();

        // extract the session
        final String session = getSessionCookie();

        // do the cleanup request
        // NB: this is done with a direct HTTP call because the attack breaks the HTML syntax
        // and JWebUnit gets crazy for that (basically there is not way to access the hidden field to edit)
        final RequestBody formBody = new FormBody.Builder()
                .add("fname", "name")
                .add("mi", "s")
                .add("lname", "surname")
                .add("username", "3")
                .add("editstudent", "1")
                .add("studentid", "1")
                .add("page", "1")
                .add("page2", "2")
                .add("logout", "")
                .build();
        final Request request = new Request.Builder()
                .url(Helper.BASE_URL + "index.php")
                .header("Cookie", session)
                .post(formBody)
                .build();
        final Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
    }
}
