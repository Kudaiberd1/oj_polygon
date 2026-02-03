package com.polygon.onlinejudge.policy;

import com.polygon.onlinejudge.entities.ProblemVersion;
import com.polygon.onlinejudge.entities.enums.Status;
import com.polygon.onlinejudge.repositories.ProblemVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProblemVersionPolicy {
    private final ProblemVersionRepository problemVersionRepository;

    @Transactional(readOnly = true)
    public void requireLatestVersionVerified(UUID problemId){
        ProblemVersion latest = problemVersionRepository
                .findFirstByProblem_IdOrderByVersionDesc(problemId)
                .orElse(null);

        if (latest == null) return;

        if (latest.getStatus() != Status.VERIFIED) {
            throw new IllegalArgumentException("Latest problem version is not VERIFIED");
        }
    }

    public void checkVersion(ProblemVersion problemVersion){

        if(problemVersion.getStatus() != Status.DRAFT) {
            throw new IllegalStateException("Problem version with id: " + problemVersion.getId() + " is already verified, you cannot change");
        }

    }
}
