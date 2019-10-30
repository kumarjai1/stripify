package com.example.stripify.service;

import com.example.stripify.model.Song;
import com.example.stripify.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    SongRepository songRepository;

    @Override
    public Song createSong(Song song) {
        return songRepository.save(song);
    }

    @Override
    public Iterable<Song> listSongs() {
        return songRepository.findAll();
    }

    @Override
    public Song getSong(Long songId) {
        return songRepository.findById(songId).orElse(null);
    }

}
