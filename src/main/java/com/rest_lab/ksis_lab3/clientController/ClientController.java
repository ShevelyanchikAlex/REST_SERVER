package com.rest_lab.ksis_lab3.clientController;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;


@RestController
public class ClientController {

    @GetMapping("/")
    public void index(HttpServletResponse response) {
        ObjectOutputStream oos = null;
        try {
            File file = new File(new File("").getAbsolutePath() + "/files");
            oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(file.getName());
            oos.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (oos != null) oos.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    @GetMapping("/file")
    public void getFile(@RequestParam String path, HttpServletResponse response) {
        ObjectOutputStream oos = null;
        BufferedInputStream bis = null;
        try {
            File file = new File(new File("").getAbsolutePath() + path);
            oos = new ObjectOutputStream(response.getOutputStream());
            if (file.isDirectory()) {
                oos.writeBoolean(true);
                oos.flush();
                String[] listOfFiles = new String[Objects.requireNonNull(file.listFiles()).length];
                for (int i = 0; i < listOfFiles.length; i++) {
                    listOfFiles[i] = Objects.requireNonNull(file.listFiles())[i].getName();
                }
                oos.writeObject(listOfFiles);
            } else {
                oos.writeBoolean(false);
                oos.flush();
                bis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[4096];
                int bytesCount = bis.read(buffer);
                while (bytesCount != -1) {
                    oos.write(buffer);
                    bytesCount = bis.read(buffer);
                }
            }
            oos.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (oos != null) oos.close();
                if (bis != null) bis.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @GetMapping("/isFile")
    public void isFile(@RequestParam String path, HttpServletResponse response) {
        ObjectOutputStream oos = null;
        try {
            File file = new File(new File("").getAbsolutePath() + path);
            oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeBoolean(file.isDirectory());
            oos.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (oos != null) oos.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @PostMapping("/file")
    public void addText(@RequestParam String path, @RequestParam String text) {
        writeText(path, text, true);
    }

    @PutMapping("/file")
    public void setText(@RequestParam String path, @RequestParam String text) {
        writeText(path, text, false);
    }

    @DeleteMapping("/file")
    public void deleteFile(@RequestParam String path, HttpServletResponse response) {
        response.setStatus(new File(new File("").getAbsolutePath() + path).delete() ? 200 : 501);
    }

    @PostMapping("/copyfile")
    public void copyFile(@RequestParam String sourcePath, @RequestParam String destPath,
                         HttpServletResponse response) {
        try {
            Files.copy(Paths.get(new File("").getAbsolutePath() + sourcePath),
                    Paths.get(new File("").getAbsolutePath() + destPath));
            response.setStatus(200);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            response.setStatus(569);
        }
    }

    @PostMapping("/movefile")
    public void moveFile(@RequestParam String sourcePath, @RequestParam String destPath,
                         HttpServletResponse response) {
        try {
            Files.move(Paths.get(new File("").getAbsolutePath() + sourcePath),
                    Paths.get(new File("").getAbsolutePath() + destPath));
            response.setStatus(200);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            response.setStatus(569);
        }
    }

    private void writeText(String path, String text, boolean append) {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(new File("").getAbsolutePath() + path, append))) {
            bos.write(text.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


}
