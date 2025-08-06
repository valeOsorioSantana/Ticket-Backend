package com.ticketlite.demo.service;

import com.ticketlite.demo.DTO.SatisfactionSurveyRequestDTO;
import com.ticketlite.demo.model.SatisfactionSurveyEntity;
import com.ticketlite.demo.model.repository.SatisfactionSurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SatisfactionSurveyService {
    private final SatisfactionSurveyRepository repository;

    @Autowired
    public SatisfactionSurveyService(SatisfactionSurveyRepository repository) {
        this.repository = repository;
    }

    public SatisfactionSurveyEntity saveOrUpdateSurvey(SatisfactionSurveyRequestDTO dto) {
        Optional<SatisfactionSurveyEntity> existingSurvey = repository.findByEventIdAndUserId(dto.getEventId(), dto.getUserId());

        SatisfactionSurveyEntity survey;

        if (existingSurvey.isPresent()) {
            // Update existing
            survey = existingSurvey.get();
            survey.setRating(dto.getRating());
            survey.setComments(dto.getComments());
            survey.setSubmittedAt(LocalDateTime.now());
        } else {
            // Create new
            survey = new SatisfactionSurveyEntity();
            survey.setEventId(dto.getEventId());
            survey.setUserId(dto.getUserId());
            survey.setRating(dto.getRating());
            survey.setComments(dto.getComments());
            survey.setSubmittedAt(LocalDateTime.now());
        }

        return repository.save(survey);
    }

}
