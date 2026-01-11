package com.compiler.controller;

import com.compiler.dto.SubmissionRequest;
import com.compiler.dto.SubmissionResponse;
import com.compiler.model.Submission;
import com.compiler.repository.SubmissionRepository;
import com.compiler.rabbitmq.ExecutionProducer;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SubmissionController {

    private final SubmissionRepository repository;
    private final ExecutionProducer producer;

    public SubmissionController(SubmissionRepository repository,
                                ExecutionProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    // ✅ FIXED SUBMIT ENDPOINT
    @PostMapping("/submit")
    public SubmissionResponse submit(@RequestBody SubmissionRequest request) {

        Submission submission = new Submission();
        submission.setLanguage(request.getLanguage());
        submission.setCode(request.getCode());
        submission.setInput(request.getInput());
        submission.setStatus("PENDING");
        submission.setCreatedAt(LocalDateTime.now());

        Submission saved = repository.save(submission);

        // send to RabbitMQ
        producer.send(saved.getId());

        SubmissionResponse response = new SubmissionResponse();
        response.setSubmissionId(saved.getId());
        response.setStatus(saved.getStatus());

        return response;
    }

    // ✅ POLLING ENDPOINT (UNCHANGED)
    @GetMapping("/submissions/{id}")
    public Submission getSubmission(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }
}
