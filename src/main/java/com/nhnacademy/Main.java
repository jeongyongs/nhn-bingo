package com.nhnacademy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ch.qos.logback.core.net.server.Client;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("s", "server", false, "Server인지 선택합니다.");
        options.addOption("p", "port", true, "port를 설정합니다.");
        options.addOption("h", "hostname", true, "hostname을 설정합니다.");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;

        try {
            commandLine = parser.parse(options, args);
            int port = 1234;
            String hostName = "localhost";
            
            if (commandLine.hasOption("p")) {
                port = Integer.parseInt(commandLine.getOptionValue("p"));
            }
            if (commandLine.hasOption("h")) {
               hostName = commandLine.getOptionValue("h");
            }

            if(commandLine.hasOption("s")) {
                Sever.from(port).run();
            } else {
                Client.of(hostName, port).run();
            }
            
        } catch (ParseException e) {
            System.out.println("잘못된 옵션입니다.");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("manual", options);
        }

    }
}