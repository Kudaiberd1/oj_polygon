package com.polygon.onlinejudge.mappers;

import com.polygon.onlinejudge.dto.test.TestGroupResponse;
import com.polygon.onlinejudge.entities.TestGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TestCaseMapper.class)
public interface TestGroupMapper {

    @Mapping(target = "problemVersionId", source = "version.id")
    @Mapping(target = "tests", source = "tests")
    TestGroupResponse toDto(TestGroup testGroup);
}
