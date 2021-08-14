package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Parking System.
 * A command line application to manage the parking system.
 */
public final class App {

    /**
     * Recovery of our logger.
     */
    private static final Logger logger = LogManager.getLogger("App");

    /**
     * @param args
     * The entry point of the program.
     */
    public static void main(String[] args) {
        //Informative log
        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
