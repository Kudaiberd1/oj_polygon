package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.test.TestCaseResponse;
import com.polygon.onlinejudge.entities.TestCase;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestCaseMapper {
    TestCaseResponse toDto(TestCase testCase);
    List<TestCaseResponse> toDtoList(List<TestCase> tests);
}
