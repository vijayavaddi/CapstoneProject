package com.hcltech.doctorpatient.service;

import com.hcltech.doctorpatient.dao.service.UserDao;
import com.hcltech.doctorpatient.dto.UserDTO;
import com.hcltech.doctorpatient.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserDao userDAO;
    private final ModelMapper modelMapper;

    public UserService (UserDao userDAO, ModelMapper modelMapper){
        this.userDAO = userDAO;
        this.modelMapper = modelMapper;
    }
    public UserDTO createUser(UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        User savedUser = userDAO.createUser(user);
        return convertToDto(savedUser);
    }
    public List<UserDTO> getAllUsers() {
        return userDAO.getAllUsers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public UserDTO getUserById(UUID id){
        User user = userDAO.getUserById(id);
        return convertToDto(user);
    }
    public void deleteUser(UUID id) {
        userDAO.deleteUser(id);
    }

    public UserDTO updateUser(UserDTO userDTO, UUID id) {
        User updatedUser = convertToEntity(userDTO);
        User savedUser = userDAO.updateUser(updatedUser, id);
        return convertToDto(savedUser);
    }

//    public UserDTO getUserById(UUID id) {
//        return userDAO.getUserById(id);
//    }


    private UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

}