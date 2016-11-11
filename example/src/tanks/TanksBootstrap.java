package tanks;

import tanks.entity.Tank;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by william on 10/30/16.
 */
public class TanksBootstrap {

    public static void main(String... args) throws URISyntaxException, IOException {
        if (args.length > 0 && args[0].equals("--skip-bootstrap")) {
            System.out.println("--skip-bootstrap set, skipping...");
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            Tanks.main(newArgs);
            return;
        }

        File file = new File(TanksBootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        System.out.println("File: " + file);

        if (!file.isFile()) {
            System.out.println("File is not a file! skipping...");
            Tanks.main(args);
            return;
        }

        String jvm_location;
        if (System.getProperty("os.name").startsWith("Win")) {
            jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
        } else {
            jvm_location = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        }

        System.out.println("Java Location: " + jvm_location);

        Runtime.getRuntime().exec(new String[]{
                jvm_location,
                "-XX:+UseConcMarkSweepGC",
                "-XX:+UseParNewGC",
                "-jar", file.getAbsolutePath(),
                "--skip-bootstrap"
        });

    }

}
