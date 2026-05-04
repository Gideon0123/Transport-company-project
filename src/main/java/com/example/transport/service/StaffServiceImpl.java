package com.example.transport.service;

import com.example.transport.dto.*;
import com.example.transport.enums.RoleType;
import com.example.transport.enums.UserStatus;
import com.example.transport.enums.UserType;
import com.example.transport.exception.BadRequestException;
import com.example.transport.exception.ResourceNotFoundException;
import com.example.transport.mapper.StaffMapper;
import com.example.transport.model.CustomerTrip;
import com.example.transport.model.Staff;
import com.example.transport.model.User;
import com.example.transport.payload.PagedResponse;
import com.example.transport.repository.StaffRepository;
import com.example.transport.repository.UserRepository;
import com.example.transport.repository.specification.BookingSearchSpecs;
import com.example.transport.repository.specification.GenericSearchSpecification;
import com.example.transport.repository.specification.StaffSearchSpecs;
import com.example.transport.util.CacheKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService{

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @CacheEvict(value = CacheKeys.STAFF, allEntries = true)
    public StaffResponseDTO createStaff(CreateStaffRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNo(dto.getPhoneNo());
        user.setUserType(UserType.STAFF);
        user.setRoleType(RoleType.valueOf(String.valueOf(dto.getRoleType()).toUpperCase()));

        Staff staff = Staff.builder()
                .user(user)
                .roleType(RoleType.valueOf(String.valueOf(dto.getRoleType()).toUpperCase()))
                .nin(dto.getNin())
                .guarantorName(dto.getGuarantorName())
                .guarantorAddress(dto.getGuarantorAddress())
                .guarantorEmail(dto.getGuarantorEmail())
                .guarantorPhone(dto.getGuarantorPhone())
                .bankName(dto.getBankName())
                .bankAccountNo(dto.getBankAccountNo())
                .salary(dto.getSalary())
                .build();

        user.setStaff(staff);

        userRepository.save(user);

        return StaffMapper.toDTO(staff);
    }

    @Override
    @Cacheable(value = CacheKeys.STAFF, key = "#page + '-' + #size + '-' + #sortBy")
    public PagedResponse<StaffSummaryDTO> getPagedStaffs(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<StaffSummaryDTO> staffPage = staffRepository.findAllStaffOptimized(pageable);

        return new PagedResponse<>(staffPage);
    }

    @Override
    @Cacheable(value = CacheKeys.STAFF, key = "#id")
    public StaffResponseDTO getStaff(Long id) {

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        return StaffMapper.toDTO(staff);
    }

    @Override
    @CacheEvict(value = CacheKeys.STAFF, allEntries = true)
    public StaffResponseDTO updateStaff(Long id, UpdateStaffRequestDTO dto) {

        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        if (dto.getRoleType() != null) {
            existingStaff.setRoleType(dto.getRoleType());
        }
        if (dto.getUserStatus() != null) {
            existingStaff.setStatus(dto.getUserStatus());
        }
        if (dto.getNin() != null) {
            existingStaff.setNin(dto.getNin());
        }
        if (dto.getGuarantorName() != null) {
            existingStaff.setGuarantorName(dto.getGuarantorName());
        }
        if (dto.getGuarantorAddress() != null) {
            existingStaff.setGuarantorAddress(dto.getGuarantorAddress());
        }
        if (dto.getGuarantorPhone() != null) {
            existingStaff.setGuarantorPhone(dto.getGuarantorPhone());
        }
        if (dto.getGuarantorEmail() != null) {
            existingStaff.setGuarantorEmail(dto.getGuarantorEmail());
        }
        if (dto.getBankName() != null) {
            existingStaff.setBankName(dto.getBankName());
        }
        if (dto.getBankAccountNo() != null) {
            existingStaff.setBankAccountNo(dto.getBankAccountNo());
        }
        if (dto.getSalary() != null) {
            existingStaff.setSalary(dto.getSalary());
        }

        User user = existingStaff.getUser();

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getPhoneNo() != null) {
            user.setPhoneNo(dto.getPhoneNo());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getUserStatus() != null) {
            user.setStatus(dto.getUserStatus());
        }

        if (dto.getRoleType() != null) {
            user.setRoleType(dto.getRoleType());
        }

        return StaffMapper.toDTO(staffRepository.save(existingStaff));
    }

    @Override
    @CacheEvict(value = CacheKeys.STAFF
            , allEntries = true)
    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot Delete: Staff not found");
        }

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        User user = staff.getUser();
        staff.setDeleted(true);
        staff.setStatus(UserStatus.INACTIVE);

        userRepository.delete(user);
    }

    @Override
    public Page<StaffResponseDTO> searchStaff(
            String keyword,
            String roleType,
            String nin,
            String bankName,
            String bankAccountNo,
            BigDecimal salary,
            Pageable pageable) {

        Map<String, Object> filters = new HashMap<>();

        if (roleType != null && !roleType.isBlank()) {
            try {
                filters.put("roleType", RoleType.valueOf(roleType.toUpperCase().trim()));
            } catch (IllegalArgumentException ignored) {}
        }

        if (nin != null && !nin.isEmpty()) {
            filters.put("nin", nin);
        }

        if (bankName != null && !bankName.isEmpty()) {
            filters.put("bankName", bankName);
        }

        if (bankAccountNo != null && !bankAccountNo.isEmpty()) {
            filters.put("bankAccountNo", bankAccountNo);
        }

        if (salary != null) {
            filters.put("salary", salary);
        }

        Specification<Staff> spec =
                new GenericSearchSpecification<Staff>().build(filters);

        if (keyword != null && keyword.length() >= 3) {
            Specification<Staff> keywordSpec =
                    StaffSearchSpecs.keywordSearch(keyword);

            spec = (spec == null)
                    ? keywordSpec
                    : spec.and(keywordSpec);
        }

        Page<Staff> staffPage =
                staffRepository.findAll(spec, pageable);

        return staffPage.map(StaffMapper::toDTO);

//        Specification<Staff> spec = Specification.allOf();
//
//        if (keyword != null && keyword.length() >= 3) {
//            spec = spec.and(StaffSearchSpecs.keywordSearch(keyword));
//        }
//
//        if (roleType != null && !roleType.isEmpty()) {
//            spec = spec.and(StaffSearchSpecs.hasRoleType(roleType));
//        }
//
//        if (nin != null && !nin.isEmpty()) {
//            spec = spec.and(StaffSearchSpecs.hasNin(nin));
//        }
//
//        if (bankName != null && !bankName.isEmpty()) {
//            spec = spec.and(StaffSearchSpecs.hasBankName(bankName));
//        }
//
//        if (bankAccountNo != null && !bankAccountNo.isEmpty()) {
//            spec = spec.and(StaffSearchSpecs.hasBankAccountNo(bankAccountNo));
//        }
//
//        if (salary != null) {
//            spec = spec.and((StaffSearchSpecs.hasSalary(salary)));
//        }
//
//        Page<Staff> staffs = staffRepository.findAll(spec, pageable);
//        return staffs.map(StaffMapper::toDTO);
    }

    @Override
    public Page<StaffResponseDTO> getDrivers(int page, int size, String sortBy) {

        List<String> allowed = List.of("staffId", "nin");

        if (!allowed.contains(sortBy)) {
            throw new BadRequestException("Invalid sort field");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Staff> drivers = staffRepository.findByRoleType(RoleType.DRIVER, pageable);
        return drivers.map(StaffMapper::toDTO);
    }

    @Override
    public Page<StaffResponseDTO> getTicketers(int page, int size, String sortBy) {

        List<String> allowed = List.of("staffId", "nin");

        if (!allowed.contains(sortBy)) {
            throw new BadRequestException("Invalid sort field");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Staff> ticketers = staffRepository.findByRoleType(RoleType.TICKETER, pageable);
        return ticketers.map(StaffMapper::toDTO);
    }

    @Override
    public Page<StaffResponseDTO> getManagers(int page, int size, String sortBy) {

        List<String> allowed = List.of("staffId", "nin");

        if (!allowed.contains(sortBy)) {
            throw new BadRequestException("Invalid sort field");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Staff> managers = staffRepository.findByRoleType(RoleType.MANAGER, pageable);
        return managers.map(StaffMapper::toDTO);
    }

    @Override
    public Page<StaffResponseDTO> getAdmins(int page, int size, String sortBy) {

        List<String> allowed = List.of("staffId", "nin");

        if (!allowed.contains(sortBy)) {
            throw new BadRequestException("Invalid sort field");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Staff> admin = staffRepository.findByRoleType(RoleType.ADMIN, pageable);
        return admin.map(StaffMapper::toDTO);
    }

}
