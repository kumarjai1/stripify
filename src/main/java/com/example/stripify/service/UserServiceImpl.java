package com.example.stripify.service;

import com.example.stripify.model.Song;
import com.example.stripify.model.User;
import com.example.stripify.model.UserRole;
import com.example.stripify.repository.UserRepository;
import com.example.stripify.util.JwtResponse;
import com.example.stripify.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    SongService songService;

    @Autowired
    @Qualifier("encoder")
    PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public Iterable<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public JwtResponse signup(User user) {
        UserRole userRole = userRoleService.getRole(user.getUserRole().getName());
        user.setUserRole(userRole);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        if (savedUser != null) {
            UserDetails userDetails = loadUserByUsername(savedUser.getUsername());
            return new JwtResponse(jwtUtil.generateToken(userDetails), savedUser.getUsername());
        }
        return null; // TODO: throw some more sensible exception
    }

    @Override
    public String login(User user) {
        User foundUser = userRepository.findByUsername(user.getUsername());

        if (foundUser != null && bCryptPasswordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            UserDetails userDetails = loadUserByUsername(foundUser.getUsername());
            return jwtUtil.generateToken(userDetails);
        }
        return null; //TODO: throw an exception
    }

    @Override
    public Long deleteById(Long userId) {
        userRepository.deleteById(userId);
        return userId;
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateUser(User user) {
        // TODO: when we update user, we need to generate a NEW JWT and return to client
        return userRepository.save(user);
    }

    @Override
    public List<Song> addSong(String username, Long songId) throws Exception {
        User user = getUser(username);
        Song song = songService.getSong(songId);
        if (song == null) throw new Exception(); // TODO: Make custom exception (e.g. EntityNotFoundException
        List<Song> userSongs = user.addSong(song);
        userRepository.save(user);
        return userSongs;
    }

    // Gets the user by username, then returns a userdetails.User object with provided username and encoding of provided password
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUser(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                true, true, true, true, getGrantedAuthorities(user));

    }

    private List<GrantedAuthority> getGrantedAuthorities(User user){
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        authorities.add(new SimpleGrantedAuthority(user.getUserRole().getName()));

        return authorities;
    }
}
