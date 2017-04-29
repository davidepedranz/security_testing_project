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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    public void assertRawText(String s) {
        assertTrue(tester.getPageSource().contains(s));
    }

    public void assertNoRawText(String s) {
        assertFalse(tester.getPageSource().contains(s));
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
     * Perform a login from the home page ignoring possible JavaScript alerts.
     *
     * @param username Username.
     * @param password Password.
     */
    public void loginIgnoreAlerts(String username, String password) {
        // NB: we disable javascript because it may be possible to get Javascript alerts
        // after some attack... since we need to login as admin to cleanup, we ignore possible
        // javascript alert dialogs
        tester.setScriptingEnabled(false);
        tester.beginAt("index.php");
        tester.setScriptingEnabled(true);
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
     * Add an hidden form filed to pass an extra POST variable to the server.
     * This method is useful to bypass client side JavaScript validation code.
     *
     * @param formName Name of the form to submit.
     * @param name     Name of the hidden form field.
     * @param value    Value of the hidden for field.
     */
    public void addHiddenFormField(String formName, String name, String value) {

        // get a reference to the form to submit
        final IElement element = tester.getElementByXPath("//form[@name='" + formName + "']");
        final DomElement form = ((HtmlUnitElementImpl) element).getHtmlElement();

        // create the submit button
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "name", "", name);
        attributes.addAttribute("", "", "value", "", value);
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
     * Go to the login page.
     */
    public void goToLoginPage() {
        tester.beginAt("index.php");
        tester.assertMatch("School Name");
        tester.assertMatch("Username");
        tester.assertMatch("Password");
    }

    /**
     * Go to the Grade Report page.
     */
    public void goToGradeReport() {

        // this page is for the administrator
        loginAsAdmin();

        // move to the page
        tester.clickLinkWithText("Students");
        tester.assertMatch("Manage Students");
        tester.setWorkingForm("students");
        tester.selectOption("report", "Grade Report");
        tester.assertMatch("Grade Report");
    }

    /**
     * Go to the Points Report page.
     */
    public void goToPointsReport() {

        // this page is for the administrator
        loginAsAdmin();

        // move to the page
        tester.clickLinkWithText("Students");
        tester.assertMatch("Manage Students");
        tester.setWorkingForm("students");
        tester.selectOption("report", "Points Report");
        tester.assertMatch("Points Report");
    }

    /**
     * Go to the page to edit the school.
     */
    public void goToEditSchool() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the school
        tester.clickLinkWithText("School");
        tester.assertMatch("Manage School Information");
    }

    /**
     * Edit the school.
     */
    public void editSchool(String message, String text, String address, String phone) {

        // verify the inputs
        assert address.length() <= 50 : "Max length for address is 50";
        assert phone.length() <= 14 : "Max length for phone is 14";

        // go to the right page
        goToEditSchool();

        // edit the parents (vulnerable form)
        tester.setWorkingForm("info");
        tester.setTextField("sitemessage", message);
        tester.setTextField("sitetext", text);
        tester.setTextField("schooladdress", address);
        tester.setTextField("schoolphone", phone);
        tester.clickButtonWithText(" Update ");
    }

    /**
     * Restore the initial status of the school.
     */
    public void cleanupSchool() throws IOException {

        // login as admin
        loginIgnoreAlerts(environment.adminUsername(), environment.adminPassword());

        // extract the session
        final String session = getSessionCookie();

        // do the cleanup request
        // NB: this is done with a direct HTTP call because the attack breaks the HTML syntax and JWebUnit gets crazy
        final RequestBody formBody = new FormBody.Builder()
                .add("schoolname", "School Name")
                .add("schooladdress", "1,Street")
                .add("schoolphone", "52365895")
                .add("numsemesters", "0")
                .add("numperiods", "0")
                .add("apoint", "0")
                .add("bpoint", "0")
                .add("cpoint", "0")
                .add("dpoint", "0")
                .add("epoint", "0")
                .add("fpoint", "0")
                .add("sitetext", "")
                .add("sitemessage", "")
                .add("infoupdate", "1")
                .add("page", "1")
                .add("page2", "1")
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
     * Go to the page to add a new semester.
     */
    public void goToAddSemester() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Semesters");
        tester.assertMatch("Manage Semesters");
        tester.clickButtonWithText("Add");
        tester.assertMatch("Add New Semester");
    }

    /**
     * Go to the page to edit the default semester.
     */
    public void goToEditSemester() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the semester
        tester.clickLinkWithText("Semesters");
        tester.assertMatch("Manage Semesters");
        tester.setWorkingForm("semesters");
        tester.checkCheckbox("delete[]");
        tester.clickButtonWithText("Edit");
        tester.assertMatch("Edit Semester");
    }

    /**
     * Modify the title of the test Semester.
     *
     * @param title New title for the semester.
     */
    public void editTestSemester(String title) {

        // go to the right page
        goToEditSemester();

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
     * Go to the page to add a new parent.
     */
    public void goToAddParent() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Parents");
        tester.assertMatch("Manage Parents");
        tester.clickButtonWithText("Add");
        tester.assertMatch("Add New Parent");
    }

    /**
     * Go to the page to edit a parent.
     */
    public void goToEditParent() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit
        tester.clickLinkWithText("Parents");
        tester.assertMatch("Manage Parents");
        tester.setWorkingForm("parents");
        tester.checkCheckbox("delete[]");
        tester.clickButtonWithText("Edit");
        tester.assertMatch("Edit Parent");
    }

    /**
     * Modify the title of the test parent.
     *
     * @param firstName  New first name for the parent.
     * @param secondName New second name for the parent.
     */
    public void editTestParent(String firstName, String secondName) {

        // go to the right page
        goToEditParent();

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
     * Go to the page to add a new student.
     */
    public void goToAddStudent() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Students");
        tester.assertMatch("Manage Students");
        tester.clickButtonWithText("Add");
        tester.assertMatch("Add New Student");
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
    public void cleanupTestStudent() throws IOException {

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
     * Go to the page to add a new teacher.
     */
    public void goToAddTeacher() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Teachers");
        tester.assertMatch("Manage Teachers");
        tester.clickButtonWithText("Add");
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
     * Go to the page to edit user #2.
     */
    public void goToEditUser() {
        goToEditUser(2);
    }

    /**
     * Go to the page to edit one user.
     */
    public void goToEditUser(int id) {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Users");
        tester.assertMatch("Manage Users");
        tester.setWorkingForm("users");
        tester.checkCheckbox("delete[]", String.valueOf(id));      // there are many default users
        tester.clickButtonWithText("Edit");
    }

    /**
     * Modify the default test user #2.
     *
     * @param username New username.
     * @param password New password.
     */
    public void editTestUser(String username, String password) {
        editTestUser(2, username, password);
    }

    /**
     * Modify one user.
     *
     * @param id       User id.
     * @param username New username.
     * @param password New password.
     */
    public void editTestUser(int id, String username, String password) {

        // go to the right page
        goToEditUser(id);

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
     * Go to the page to edit the default class.
     */
    public void goToEditClass() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page to edit the student
        tester.clickLinkWithText("Classes");
        tester.assertMatch("Manage Classes");
        tester.setWorkingForm("classes");
        tester.checkCheckbox("delete[]");
        tester.clickButtonWithText("Edit");
    }

    /**
     * Modify the default class.
     *
     * @param name    New name.
     * @param section New section.
     * @param room    New room.
     * @param period  New period.
     */
    public void editTestClass(String name, String section, String room, String period) {

        // go to the right page
        goToEditClass();

        // edit (vulnerable form)
        tester.setWorkingForm("editclass");
        tester.setTextField("title", name);
        tester.setTextField("sectionnum", section);
        tester.setTextField("roomnum", room);
        tester.setTextField("periodnum", period);
        tester.clickButtonWithText("Edit Class");
    }

    /**
     * Restore the initial status of the test class.
     */
    public void cleanupTestClass() {
        editTestClass("course", "section", "room", "ppp");
    }

    /**
     * Go to the page to edit the default term.
     */
    public void goToEditTerm() {

        // this is a stored XSS vulnerability... login as admin
        loginAsAdmin();

        // move to the page
        tester.clickLinkWithText("Term");
        tester.assertMatch("Manage Term");
        tester.setWorkingForm("terms");
        tester.checkCheckbox("delete[]");
        tester.clickButtonWithText("Edit");
        tester.assertMatch("Edit Term");
    }

    /**
     * Modify the title of the test term.
     *
     * @param title New title for the term.
     */
    public void editTestTerm(String title) {

        // go to the right page
        goToEditTerm();

        // edit (vulnerable form)
        tester.setWorkingForm("editterm");
        tester.setTextField("title", title);   // max 15 characters!
        tester.clickButtonWithText("Edit Term");
    }

    /**
     * Restore the initial status of the test term.
     */
    public void cleanupTestTerm() {
        editTestTerm("term");
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
