package com.example.stripify.service;

import com.example.stripify.model.Song;

public interface SongService {

    Song createSong(Song song);
    Iterable<Song> listSongs();

    Song getSong(Long songId);
}
