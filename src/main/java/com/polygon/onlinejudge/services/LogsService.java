package com.polygon.onlinejudge.services;

import com.polygon.onlinejudge.entities.Logs;
import com.polygon.onlinejudge.repositories.LogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogsService {

    private final LogsRepository logsRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Logs log) {
        logsRepository.save(log);
    }
}
