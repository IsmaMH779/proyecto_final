package com.example.UserManagementService.service;

import com.example.UserManagementService.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizerService {
    @Autowired
    private OrganizerRepository organizerRepository;


}
