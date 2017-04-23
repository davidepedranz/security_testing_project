package tests.utilities;

import org.aeonbits.owner.Config;

/**
 * Helper class that defines the constant that depends on the particular installation of SchoolMate,
 * like the server base URL or the default users that the suite tests expects.
 *
 * @author Davide Pedranz (pedranz@fbk.eu)
 */
public interface Environment extends Config {

    @Key("BASE_URL")
    @DefaultValue("http://localhost:8000/")
    String baseURL();


    @DefaultValue("test")
    String adminUsername();

    @DefaultValue("test")
    String adminPassword();

    @DefaultValue("teacher")
    String teacherUsername();

    @DefaultValue("teacher")
    String teacherPassword();

    @DefaultValue("student")
    String studentUsername();

    @DefaultValue("student")
    String studentPassword();

    @DefaultValue("parent")
    String parentUsername();

    @DefaultValue("parent")
    String parentPassword();
}
