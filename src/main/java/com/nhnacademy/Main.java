package com.nhnacademy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.nhnacademy.client.Client;
import com.nhnacademy.server.Server;

public class Main {
    private static final int DEFAULT_PORT = 1234;
    private static final String DEFAULT_HOSTNAME = "localhost";

    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("s", "server", false, "Server인지 선택합니다.");
        options.addOption("p", "port", true, "port를 설정합니다.");
        options.addOption("h", "hostname", true, "hostname을 설정합니다.");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;

        try {
            int port = DEFAULT_PORT;
            String hostName = DEFAULT_HOSTNAME;
            commandLine = parser.parse(options, args);
            
            if (commandLine.hasOption("p")) {
                port = Integer.parseInt(commandLine.getOptionValue("p"));
            }
            if (commandLine.hasOption("h")) {
               hostName = commandLine.getOptionValue("h");
            }

            if(commandLine.hasOption("s")) {
                Server.from(port).run();
            } else {
                Client.of(hostName, port).run();
            }
            
        } catch (ParseException ignore) {
            System.out.println("잘못된 옵션입니다.");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("manual", options);
        }
    }
}