package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

  private final PatientRepository patientRepository;
  private final BillingServiceGrpcClient billingServiceGrpcClient;
  private final KafkaProducer kafkaProducer;

  public PatientService(PatientRepository patientRepository,
      BillingServiceGrpcClient billingServiceGrpcClient,
      KafkaProducer kafkaProducer) {
    this.patientRepository = patientRepository;
    this.billingServiceGrpcClient = billingServiceGrpcClient;
    this.kafkaProducer = kafkaProducer;
  }

  @Cacheable(value = "PATIENT_CACHE", key = "#email")
  public PatientResponseDTO getPatientByEmail(String email)  {
    System.out.println("Looking for: " + email);
    return patientRepository.findByEmail(email)
            .map(PatientMapper::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));
  }

  @Cacheable(value = "PATIENT_CACHE", key = "#id")
  public PatientResponseDTO getPatientById(UUID id)  {
    return patientRepository.findById(id)
            .map(PatientMapper::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
  }


  public List<PatientResponseDTO> getPatients() {
    List<Patient> patients = patientRepository.findAll();

    return patients.stream().map(PatientMapper::toDTO).toList();
  }

  public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
    if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
      throw new EmailAlreadyExistsException(
          "A patient with this email " + "already exists"
              + patientRequestDTO.getEmail());
    }

    Patient newPatient = patientRepository.save(
        PatientMapper.toModel(patientRequestDTO));

    billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
        newPatient.getName(), newPatient.getEmail());

    kafkaProducer.sendEvent(newPatient);

    return PatientMapper.toDTO(newPatient);
  }

  public PatientResponseDTO updatePatient(UUID id,
      PatientRequestDTO patientRequestDTO) {

    Patient patient = patientRepository.findById(id).orElseThrow(
        () -> new PatientNotFoundException("Patient not found with ID: " + id));

    if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),
        id)) {
      throw new EmailAlreadyExistsException(
          "A patient with this email " + "already exists"
              + patientRequestDTO.getEmail());
    }

    patient.setName(patientRequestDTO.getName());
    patient.setAddress(patientRequestDTO.getAddress());
    patient.setEmail(patientRequestDTO.getEmail());
    patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

    Patient updatedPatient = patientRepository.save(patient);
    return PatientMapper.toDTO(updatedPatient);
  }
  @CacheEvict(value = "PATIENT_CACHE", key = "#id")
  public void deletePatient(UUID id) {
    patientRepository.deleteById(id);
  }
}
