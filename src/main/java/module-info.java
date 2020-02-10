module gw.modmanager {
    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk8;

    requires tornadofx;
    requires konsume.xml;
    requires java.xml;
    requires klaxon;

    requires javafx.controls;
    requires javafx.graphics;
    requires java.desktop;

    // for ssl. to get the db file
    requires jdk.crypto.ec;

    opens com.geowarin.modmanager.gui to tornadofx;
    exports com.geowarin.modmanager;
}
