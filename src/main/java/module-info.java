module gw.modmanager {
    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk8;

    requires tornadofx;
    requires konsume.xml;
    requires java.xml;
    requires klaxon;

    requires javafx.controls;
    requires javafx.graphics;

    // for ssl. to get the db file
    requires jdk.crypto.ec;

    exports com.geowarin.modmanager;
}
