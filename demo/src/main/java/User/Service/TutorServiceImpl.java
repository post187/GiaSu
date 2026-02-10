package User.Service;

import Review.Dto.Response.ReviewResponse;
import User.DTO.Request.AvailabilityRequest;
import User.DTO.Request.TutorUpdateRequest;
import User.DTO.Request.UnavailabilityRequest;
import User.DTO.Request.VerificationSubmitRequest;
import User.DTO.Response.*;
import User.Entity.TutorAvailability;
import User.Entity.TutorProfile;
import User.Entity.User;
import User.Entity.VerificationStatus;
import User.Mapper.TutorProfileMapper;
import User.Mapper.UserMapper;
import User.Repository.TutorAvailabilityRepository;
import User.Repository.TutorProfileRepository;
import User.Repository.UserRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorServiceImpl implements TutorService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TutorProfileMapper tutorProfileMapper;

    @Autowired
    private TutorAvailabilityRepository tutorAvailabilityRepository;

    @Autowired

    @Override
    public TutorProfileResponse getMyProfile() {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = userMapper.toUser(user);
        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(user1.getId());
        return tutorProfileMapper.toResponse(tutorProfile);
    }

    @Override
    public TutorProfileResponse updateProfile(TutorUpdateRequest request) {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = userMapper.toUser(user);
        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(user1.getId());

        tutorProfile.setBio(request.getBio());
        tutorProfile.setEducation(request.getEducation());
        tutorProfile.setCity(request.getCity());
        tutorProfile.setCertificates(request.getCertificates());
        tutorProfile.setHourlyRateMax(request.getHourlyRateMax());
        tutorProfile.setHourlyRateMin(request.getHourlyRateMin());
        tutorProfile.setTeachingModes(request.getTeachingModes());
        tutorProfile.setDistrict(request.getDistrict());
        tutorProfile.setYearsOfExperience(request.getYearsOfExperience());

        return tutorProfileMapper.toResponse(tutorProfile);
    }

    @Override
    public TutorProfileResponse submitVerification(VerificationSubmitRequest request) {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = userMapper.toUser(user);
        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(user1.getId());
        tutorProfile.setNationalIdNumber(request.getNationalIdNumber());
        tutorProfile.setVerificationSubmittedAt(LocalDateTime.now());
        tutorProfile.setNationalIdFrontImageUrl(request.getNationalIdFrontImageUrl());
        tutorProfile.setNationalIdBackImageUrl(request.getNationalIdBackImageUrl());
        tutorProfile.setProofDocuments(request.getProofDocuments().toString());
        tutorProfile.setCertificatesDetail(request.getCertificatesDetail().toString());
        tutorProfile.setVerificationStatus(VerificationStatus.PENDING);

        return tutorProfileMapper.toResponse(tutorProfile);
    }

    @Override
    @Transactional
    public List<AvailabilityResponse> updateAvailability(List<AvailabilityRequest> dtos) {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = userMapper.toUser(user);
        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(user1.getId());
        tutorAvailabilityRepository.deleteByTutorId(user1.getId());

        if (dtos != null && !dtos.isEmpty()) {
            List<TutorAvailability> newEntries = dtos.stream()
                    .map(dto -> {
                        TutorAvailability entity = new TutorAvailability();
                        entity.setTutor(tutorProfile);
                        entity.setDayOfWeek(dto.getDayOfWeek());
                        entity.setStartMinute(dto.getStartMinute());
                        entity.setEndMinute(dto.getEndMinute());
                        entity.setTimezone(dto.getTimezone());
                        return entity;
                    })
                    .collect(Collectors.toList());

            tutorAvailabilityRepository.saveAll(newEntries);
        }

        return tutorAvailabilityRepository.findByTutorIdOrderByDayOfWeekAscStartMinuteAsc(user1.getId())
                .stream()
                .map(this::mapToAvailabilityResponse)
                .collect(Collectors.toList());
    }

    private AvailabilityResponse mapToAvailabilityResponse(TutorAvailability entity) {
        // Chuyển đổi Entity ngược lại DTO
        AvailabilityResponse dto = new AvailabilityResponse();
        dto.setDayOfWeek(entity.getDayOfWeek());
        dto.setStartMinute(entity.getStartMinute());
        dto.setEndMinute(entity.getEndMinute());
        dto.setTimezone(entity.getTimezone());
        return dto;
    }

    @Override
    public TutorProfilePublicResponse getPublicProfile(String tutorId) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Id not found"));

        return tutorProfileMapper.toPublicResponse(tutorProfile);
    }

    @Override
    public TrustScoreResponse getTutorTrustScore(String tutorId) {
        TutorProfile tutor = tutorProfileRepository.findById(tutorId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor not found"));

        return TrustScoreResponse.builder()
                .averageRating(tutor.getAverageRating() != null ? tutor.getAverageRating() : 0.0)
                .trustScore(tutor.getTrustScore() != null ? tutor.getTrustScore() : 0)
                .totalReviews(tutor.getTotalReviews() != null ? tutor.getTotalReviews() : 0)
                .totalCompletedBookings(tutor.getTotalCompletedBookings() != null ? tutor.getTotalCompletedBookings() : 0)
                .build();
    }


}
