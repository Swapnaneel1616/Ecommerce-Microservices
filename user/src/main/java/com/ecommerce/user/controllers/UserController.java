package com.ecommerce.user.controllers;

import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {


    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        return new ResponseEntity<>(userService.fetchAllUsers() ,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createUsers(@RequestBody UserRequest userRequest){
        userService.addUser(userRequest);
        return new ResponseEntity<>("User is created" , HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id , @RequestBody UserRequest updateUserRequest){
        boolean updated = userService.updateUser(id , updateUserRequest);
        if(updated)
            return new ResponseEntity<>("USER UPDATED SUCCESFULLY " , HttpStatus.OK);
        return ResponseEntity.notFound().build();
    }

}
