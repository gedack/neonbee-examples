package io.neonbee.examples.cds.odata;

import java.nio.file.Paths;

import io.neonbee.Launcher;

/**
 * This Application class calls the NeonBee Launcher to start NeonBee more easily and sets the working-directory
 * directly to 'working_dir'.
 */
public class Application extends Launcher {

    public static void main(String[] args) {
        int argsCount = args.length;
        String[] extendedArgs = new String[argsCount + 2];
        System.arraycopy(args, 0, extendedArgs, 0, argsCount);

        // Add the custom working directory
        extendedArgs[argsCount] = "-working-directory";
        extendedArgs[argsCount + 1] = Paths.get("working_dir").toAbsolutePath().toString();

        // Start NeonBee via the Launchers' method
        Launcher.main(extendedArgs);
    }

}
