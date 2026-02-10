package Subject.Service;

import Subject.Dto.Request.SubjectRequest;
import Subject.Dto.Response.SubjectResponse;

import java.util.List;

public interface SubjectService {
    List<SubjectResponse> getAllSubjects();
    SubjectResponse createSubject(SubjectRequest request);
}
