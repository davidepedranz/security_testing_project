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
     * Modify the title of the test parent.
     *
     * @param firstName  New first name for the parent.
     * @param secondName New second name for the parent.
     */
    public void editTestParent(String firstName, String secondName) {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the semester
        tester.clickLinkWithText("Parents");
        tester.assertMatch("Manage Parents");
        tester.setWorkingForm("parents");
        tester.checkCheckbox("delete[]");
        tester.clickButtonWithText("Edit");

        // edit the parents (vulnerable form)
        tester.setWorkingForm("editparent");
        tester.setTextField("fname", firstName);
        tester.setTextField("lname", secondName);
        tester.clickButtonWithText("Edit parent");
    }

    /**
     * Restore the initial status of the test parent.
     */
    public void cleanupTestParent() {
        editTestParent("parent", "parent");
    }

    /**
     * Modify the student test user.
     *
     * @param fname First name.
     * @param mi    Initial.
     * @param fname Second name.
     */
    public void editTestStudent(String fname, String mi, String lname) {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Students");
        tester.assertMatch("Manage Students");
        tester.setWorkingForm("students");
        tester.checkCheckbox("delete[]");
        tester.clickButtonWithText("Edit");

        // edit the parents (vulnerable form)
        tester.setWorkingForm("editstudent");
        tester.setTextField("fname", fname);
        tester.setTextField("mi", mi);
        tester.setTextField("lname", lname);
        tester.clickButtonWithText("Edit Student");
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

    /**
     * Go to the page to edit the default teacher.
     */
    public void goToEditTeacher() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Teachers");
        tester.assertMatch("Manage Teachers");
        tester.setWorkingForm("teachers");
        tester.checkCheckbox("delete[]", "1");      // there are 2 default teachers
        tester.clickButtonWithText("Edit");
    }

    /**
     * Modify the teacher test user.
     *
     * @param fname First name.
     * @param fname Second name.
     */
    public void editTestTeacher(String fname, String lname) {

        // go to the right page
        goToEditTeacher();

        // edit the parents (vulnerable form)
        tester.setWorkingForm("editteacher");
        tester.setTextField("fname", fname);
        tester.setTextField("lname", lname);
        tester.clickButtonWithText("Edit teacher");
    }

    /**
     * Restore the initial status of the test teacher.
     */
    public void cleanupTestTeacher() {
        editTestTeacher("teacher", "teacher");
    }

    /**
     * Go to the page to edit the default user #2.
     */
    public void goToEditUser() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Users");
        tester.assertMatch("Manage Users");
        tester.setWorkingForm("users");
        tester.checkCheckbox("delete[]", "2");      // there are many default users
        tester.clickButtonWithText("Edit");
    }

    /**
     * Modify the default test user #2.
     *
     * @param username New username.
     */
    public void editTestUser(String username, String password) {

        // go to the right page
        goToEditUser();

        // edit (vulnerable form)
        tester.setWorkingForm("edituser");
        tester.setTextField("username", username);
        tester.setTextField("password", password);
        tester.setTextField("password2", password);
        tester.clickButtonWithText("Edit user");
    }

    /**
     * Restore the initial status of the test user #2.
     */
    public void cleanupTestUser() {
        editTestUser("teacher", "teacher");
    }

    /**
     * Modify the default test announcement.
     *
     * @param title   Title.
     * @param message Message.
     */
    public void edtiTestAnnouncement(String title, String message) {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Announcements");
        tester.assertMatch("Manage Announcements");
        tester.setWorkingForm("announcements");
        tester.checkCheckbox("delete[]");
        tester.clickButtonWithText("Edit");

        // edit the parents (vulnerable form)
        tester.setWorkingForm("editannouncement");
        tester.setTextField("title", title);
        tester.setTextField("message", message);
        tester.clickButtonWithText("Edit Announcement");
    }
}
