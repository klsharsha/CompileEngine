package com.compiler.rabbitmq;

import com.compiler.config.RabbitMQConfig;
import com.compiler.model.Submission;
import com.compiler.repository.SubmissionRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Component
public class ExecutionConsumer {

    private final SubmissionRepository repository;

    private static final int TIME_LIMIT_SECONDS = 2;
    private static final String MEMORY_LIMIT = "128m";

    public ExecutionConsumer(SubmissionRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConfig.EXECUTION_QUEUE)
    public void consume(Long submissionId) {

        Path tempDir = null;

        try {
            Submission submission = repository.findById(submissionId)
                    .orElseThrow();

            submission.setStatus("RUNNING");
            repository.save(submission);

            // create temp working directory
            tempDir = Files.createTempDirectory("exec-");

            String language = submission.getLanguage();
            String fileName = getSourceFileName(language);
            String dockerImage = getDockerImage(language);

            // write code to file
            File codeFile = tempDir.resolve(fileName).toFile();
            try (FileWriter writer = new FileWriter(codeFile)) {
                writer.write(submission.getCode());
            }

            // docker execution command
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "run", "--rm", "-i",
                    "--memory=" + MEMORY_LIMIT,
                    "--memory-swap=" + MEMORY_LIMIT,
                    "--pids-limit=64",
                    "--network", "none",
                    "-v", tempDir.toAbsolutePath() + ":/app",
                    dockerImage
            );


            pb.redirectErrorStream(true);
            Process process = pb.start();

            // ✅ PASS INPUT TO STDIN
            if (submission.getInput() != null && !submission.getInput().isBlank()) {
                try (var stdin = process.getOutputStream()) {
                    stdin.write((submission.getInput() + "\n").getBytes());
                    stdin.flush();
                }
            }


            // wait with time limit
            boolean finished = process.waitFor(TIME_LIMIT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                submission.setStatus("TIME_LIMIT_EXCEEDED");
                submission.setOutput("Execution exceeded time limit");
                repository.save(submission);
                return;
            }

            int exitCode = process.exitValue();
            String output = new String(process.getInputStream().readAllBytes());

            // ---------- RESULT CLASSIFICATION ----------

            if (exitCode == 0) {
                submission.setStatus("SUCCESS");
                submission.setOutput(output);

            } else if (exitCode == 137) {
                submission.setStatus("MEMORY_LIMIT_EXCEEDED");
                submission.setOutput("Killed (Memory limit exceeded)");

            } else if (exitCode == 139) {
                submission.setStatus("RUNTIME_ERROR");
                submission.setOutput("Segmentation fault");

            } else {
                submission.setStatus("RUNTIME_ERROR");
                submission.setOutput(output.isBlank() ? "Runtime error" : output);
            }

            repository.save(submission);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // cleanup temp directory
            if (tempDir != null) {
                try {
                    Files.walk(tempDir)
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (Exception ignored) {}
            }
        }
    }

    // ---------------- HELPERS ----------------

    private String getDockerImage(String language) {
        return switch (language.toUpperCase()) {
            case "JAVA" -> "java-runner";
            case "PYTHON" -> "python-runner";
            case "C" -> "c-runner";
            case "CPP" -> "cpp-runner";
            case "JAVASCRIPT" -> "js-runner";
            default -> throw new RuntimeException("Unsupported language: " + language);
        };
    }

    private String getSourceFileName(String language) {
        return switch (language.toUpperCase()) {
            case "JAVA" -> "Main.java";
            case "PYTHON" -> "main.py";
            case "C" -> "main.c";
            case "CPP" -> "main.cpp";
            case "JAVASCRIPT" -> "main.js";
            default -> throw new RuntimeException("Unsupported language: " + language);
        };
    }
}
