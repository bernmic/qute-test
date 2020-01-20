package de.b4;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ApplicationScoped
public class FileService {
    private static Logger LOGGER = Logger.getLogger(FileService.class);

    private final static String FILENAME_PATTERN = "billing-%s-%04d-%02d-%02d.csv";
    private final static String FILEPATH_PATTERN = "%s/" + FILENAME_PATTERN;

    @ConfigProperty(name = "data.home")
    Optional<String> dataHome;

    public void createFiles() {
        if (!dataHome.isPresent()) {
            LOGGER.error("data.home ist nicht definiert");
            return;
        }
        LocalDate d = LocalDate.now();
        for (int i = 0; i < 200; i++) {
            String s = String.format(FILEPATH_PATTERN, dataHome.get(), "dev", d.getYear(), d.getMonthValue(), d.getDayOfMonth());
            d = d.minusDays(1);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(s));
                writer.write(s);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> getAllFiles() {
        if (!dataHome.isPresent()) {
            LOGGER.error("data.home ist nicht definiert");
            return Collections.emptySet();
        }
        Set<String> result;
        try (Stream<Path> walk = Files.walk(Paths.get(dataHome.get()))) {
            result = walk.filter(Files::isRegularFile)
                    .map(x -> x.getFileName().toString()).collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.EMPTY_SET;
        }
        return result;
    }

    public SortedSet<Integer> getYears() {
        SortedSet<Integer> years = new TreeSet<>();
        for (String f : getAllFiles()) {
            String[] parts = f.split("-");
            if (parts.length == 5) {
                years.add(Integer.parseInt(parts[2]));
            }
        }
        return years;
    }

    public SortedSet<Integer> getMonths(int year) {
        SortedSet<Integer> months = new TreeSet<>();
        for (String f : getAllFiles()) {
            String[] parts = f.split("-");
            if (parts.length == 5) {
                if (year == Integer.parseInt(parts[2])) {
                    months.add(Integer.parseInt(parts[3]));
                }
            }
        }
        return months;
    }

    public SortedSet<Integer> getDays(int year, int month) {
        SortedSet<Integer> days = new TreeSet<>();
        for (String f : getAllFiles()) {
            String[] parts = f.split("-");
            if (parts.length == 5) {
                if (year == Integer.parseInt(parts[2]) && month == Integer.parseInt(parts[3])) {
                    days.add(Integer.parseInt(parts[4].substring(0, parts[4].indexOf("."))));
                }
            }
        }
        return days;
    }

    public String getDayFilename(int year, int month, int day) {
        if (!dataHome.isPresent()) {
            LOGGER.error("data.home ist nicht definiert");
            throw new WebApplicationException("Konfigurationsfehler", 500);
        }
        return String.format(FILEPATH_PATTERN, dataHome.get(), "dev", year, month, day);
    }

    public byte[] getDaysAsZip(int year, int month) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(out);
        try {
            for (Integer day : getDays(year, month)) {
                FileInputStream fis = new FileInputStream(getDayFilename(year, month, day));
                ZipEntry entry = new ZipEntry(String.format(FILENAME_PATTERN, "dev", year, month, day));
                zip.putNextEntry(entry);
                zip.write(Files.readAllBytes(Paths.get(getDayFilename(year, month, day))));
            }
            zip.close();
        } catch (IOException e) {
            throw new WebApplicationException("Datei nicht lesbar.", 404);
        }
        return out.toByteArray();
    }

    public String getDayFile(int year, int month, int day) {
        if (!dataHome.isPresent()) {
            LOGGER.error("data.home ist nicht definiert");
            throw new WebApplicationException("Konfigurationsfehler", 500);
        }
        String s = String.format(FILEPATH_PATTERN, dataHome.get(), "dev", year, month, day);
        String result = "";
        try {
            byte[] b = Files.readAllBytes(Paths.get(s));
            result = new String(b);
        } catch (IOException e) {
            LOGGER.errorf("Datei %s konnte nicht gelesen werden.", s);
            throw new WebApplicationException("Datei konnte nicht gelesen werden.", 404);
        }
        return result;
    }
}
