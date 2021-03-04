package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.config.ConverterConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.converter.CsvBinConverter;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.entity.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Objects;

public class Program {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Program arguments is undefined");
            System.exit(0);
        }

        Objects.requireNonNull(args[0], "Command is undefined");
        String command = args[0].toLowerCase();

        Objects.requireNonNull(args[1], "Source file path is undefined");
        String sourcePath = args[1].toLowerCase();

        Objects.requireNonNull(args[2], "Result file path is undefined");
        String resultPath = args[2].toLowerCase();

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConverterConfig.class);
        CsvBinConverter<User, String> converter = applicationContext.getBean(CsvBinConverter.class);

        if (command.equals("bintocsv")) {
            converter.convertBinToCsv(sourcePath, resultPath);
        } else if (command.equals("csvtobin")) {
            converter.convertCsvToBin(sourcePath, resultPath);
        } else {
            System.out.println("Invalid command");
        }
    }
}
