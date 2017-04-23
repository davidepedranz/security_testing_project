package tests.utilities;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.InputElementFactory;
import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.htmlunit.HtmlUnitElementImpl;
import net.sourceforge.jwebunit.junit.WebTester;
import okhttp3.*;
import org.xml.sax.helpers.AttributesImpl;

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

    // dependencies
    private final Environment environment;
    private final WebTester tester;
    private final OkHttpClient client;

    /**
     * Construct a new instance of the Helper.
     *
     * @param tester An instance of WebTester pointing to the vulnerable application.
     */
    public Helper(Environment environment, WebTester tester) {
        this.environment = environment;
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
        login(environment.adminUsername(), environment.adminPassword());
    }

    /**
     * Login as the Teacher test user.
     */
    public void loginAsTeacher() {
        login(environment.teacherUsername(), environment.teacherPassword());
    }

    /**
     * Login as the Student test user.
     */
    public void loginAsStudent() {
        login(environment.studentUsername(), environment.studentPassword());
    }

    /**
     * Login as the Parent test user.
     */
    public void loginAsParent() {
        login(environment.parentUsername(), environment.parentPassword());
    }

    /**
     * Add a plain submit button to the form with the given name.
     * This method is useful to bypass client side JavaScript validation code.
     *
     * @param formName Name of the form to submit.
     */
    public void addSubmitButton(String formName) {

        // get a reference to the form to submit
        final IElement element = tester.getElementByXPath("//form[@name='" + formName + "']");
        final DomElement form = ((HtmlUnitElementImpl) element).getHtmlElement();

        // create the submit button
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "type", "", "submit");
        final HtmlElement submit = InputElementFactory.instance.createElement(form.getPage(), "input", attributes);
        form.appendChild(submit);
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
     * Modify the title of the test Semester.
     *
     * @param title New title for the semester.
     */
    public void editTestSemester(String title) {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the semester
        tester.clickLinkWithText("Semesters");
        tester.assertMatch("Manage Semesters");
        tester.setWorkingForm("semesters");
        tester.checkCheckbox("delete[]");
        tester.clickButtonWithText("Edit");

        // edit the semester (vulnerable form)
        tester.setWorkingForm("editsemester");
        tester.setTextField("title", title);   // max 15 characters!
        tester.clickButtonWithText("Edit Semester");
    }

    /**
     * Restore the initial status of the test Semester.
     */
    public void cleanupTestSemester() {
        editTestSemester("semester");
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
                .url(environment.baseURL() + "index.php")
                .header("Cookie", session)
                .post(formBody)
                .build();
        final Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
    }
}
