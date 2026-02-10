package Subject.Service;

import Subject.Dto.Request.SubjectRequest;
import Subject.Dto.Response.SubjectResponse;
import Subject.Entity.Subject;
import Subject.Repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService{
    @Autowired
    private SubjectRepository subjectRepository;


    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        // Logic từ TS: orderBy: [{ level: "asc" }, { name: "asc" }]
        Sort sort = Sort.by(
                Sort.Order.asc("level"),
                Sort.Order.asc("name")
        );

        return subjectRepository.findAll(sort).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        // Validate unique name nếu cần thiết (optional, DB đã có unique constraint)
        if (subjectRepository.existsByName(request.getName())) {
            throw new RuntimeException("Subject name already exists");
        }

        Subject entity = Subject.builder()
                .name(request.getName())
                .level(request.getLevel())
                .description(request.getDescription())
                .build();

        Subject savedEntity = subjectRepository.save(entity);
        return mapToResponse(savedEntity);
    }

    // Helper mapper (Nên dùng MapStruct trong dự án thực tế)
    private SubjectResponse mapToResponse(Subject entity) {
        return SubjectResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .level(entity.getLevel())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
