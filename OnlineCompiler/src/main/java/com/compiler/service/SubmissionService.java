package com.compiler.service;

import com.compiler.dto.SubmissionRequest;
import com.compiler.dto.SubmissionResponse;
import com.compiler.model.Submission;
import com.compiler.rabbitmq.ExecutionProducer;
import com.compiler.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubmissionService {

    private final SubmissionRepository repository;
    private final ExecutionProducer producer;

    public SubmissionService(SubmissionRepository repository,
                             ExecutionProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    public SubmissionResponse submitCode(SubmissionRequest request) {

        Submission submission = new Submission(
                request.getLanguage(),
                request.getCode(),
                request.getInput(),
                null,
                "PENDING",
                LocalDateTime.now()
        );

        Submission saved = repository.save(submission);

        // Send submission id to RabbitMQ
        producer.send(saved.getId());

        return new SubmissionResponse(
                saved.getId(),
                saved.getStatus()
        );
    }
}
