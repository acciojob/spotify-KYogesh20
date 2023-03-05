package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {

        // Find the user with the given mobile exists or not
        for (User temp : users) {
            if (temp.getMobile().equals(mobile)) return temp;
        }

        // if the user is new then create the account
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        // check if the artist already exists or not
        for(Artist currArtist : artists){
            if(currArtist.getName().equals(name)) return currArtist;
        }

        // if artist does not exists in the db then create new one
        Artist newArtist = new Artist(name);
        artists.add(newArtist);
        return newArtist;
    }

    public Album createAlbum(String title, String artistName) {
        // check if the album exists already or not
        for(Album currAlbum : albums){
            if(currAlbum.getTitle().equals(title)) return currAlbum;
        }

        // create a new one if it doesnt exists
        Album newAlbum = new Album(title);
        albums.add(newAlbum);

        // create the artist
        Artist newArtist = createArtist(artistName);

        // put the artist and album in db
        List<Album> artistAlbum  = new ArrayList<>();
        if(artistAlbumMap.containsKey(newArtist)) artistAlbum = artistAlbumMap.get(newArtist);
        artistAlbum.add(newAlbum);

        // mapping the artist to album;
        artistAlbumMap.put(newArtist,artistAlbum);
        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album newAlbum = new Album();
        boolean albumExists = false;
        for(Album currAlbum: albums){
            if(currAlbum.getTitle().equals(albumName)){
                newAlbum = currAlbum;
                albumExists = true;
                break;
            }
        }

        if(!albumExists) throw new Exception("Album does not exist");

        Song newSong = new Song(title,length);
        songs.add(newSong);

        // map the song to the corrosponding map using albumssongmap
        List<Song> list = new ArrayList<>();
        if(albumSongMap.containsKey(newAlbum))
            list = albumSongMap.get(newAlbum);

        list.add(newSong);
        albumSongMap.put(newAlbum,list);

        return newSong;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {


        // if playlist exists then return it
        for(Playlist currPlaylist: playlists){
            if(currPlaylist.getTitle().equals(title)) return currPlaylist;
        }

        // create the playlist with the title
        Playlist newPlaylist = new Playlist(title);

        // add the newly created playlist to the playlists arraylst
        playlists.add(newPlaylist);

        // find the song with the length
        List<Song> tempSong = new ArrayList<>();
        for(Song currSong : songs){
            if(currSong.getLength()==length) tempSong.add(currSong);
        }
        playlistSongMap.put(newPlaylist,tempSong);


        // check if the current users exists for which playlist is being created
        User newUser = new User();
        boolean isExists = false;
        for(User currUser: users){
            if(currUser.getMobile().equals(mobile)){
                isExists = true;
                newUser = currUser;
                break;
            }
        }
        if(!isExists) throw new Exception("User does not exist");

        List<Playlist> newUserPlaylist = new ArrayList<>();
        if(userPlaylistMap.containsKey(newUser)) newUserPlaylist=userPlaylistMap.get(newUser);
        newUserPlaylist.add(newPlaylist);
        userPlaylistMap.put(newUser,newUserPlaylist);
        return newPlaylist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        // check if playlist already exists or not
        for(Playlist currPlaylist: playlists){
            if(currPlaylist.getTitle().equals(title)) return currPlaylist;
        }

        // create new playlist if playlist doesnt exists
        Playlist newPlaylist = new Playlist(title);

        // add the newly created playlist to the playlists arraylist
        playlists.add(newPlaylist);

        // find the song from songTitles
        List<Song> newSong = new ArrayList<>();
        for(Song currSong: songs){
            String songTitle = currSong.getTitle();
            if(songTitles.contains(songTitle)) newSong.add(currSong);
        }
        playlistSongMap.put(newPlaylist,newSong);

        // check if the users exists or not to map the playlist to user
        User newUser = new User();
        boolean isExists = false;
        for(User currUser: users){
            if(currUser.getMobile().equals(mobile)){
                isExists = true;
                newUser = currUser;
                break;
            }
        }
        if(!isExists) throw new Exception("User does not exist");

        List<User> list = new ArrayList<>();
        if(playlistListenerMap.containsKey(newPlaylist)){
            list = playlistListenerMap.get(newPlaylist);
        }
        list.add(newUser);
        playlistListenerMap.put(newPlaylist,list);

        List<Playlist> userPlaylists = new ArrayList<>();
        if(userPlaylistMap.containsKey(newUser)){
            userPlaylists=userPlaylistMap.get(newUser);
        }
        userPlaylists.add(newPlaylist);
        userPlaylistMap.put(newUser,userPlaylists);

        return newPlaylist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        // check if the playlist exists or not
        boolean isExists = false;
        Playlist newPlaylist = new Playlist();
        for(Playlist currPlaylist : playlists){
            String currPlaylistTitle = currPlaylist.getTitle();
            if(currPlaylistTitle.equals(playlistTitle)){
                isExists = true;
                newPlaylist = currPlaylist;
            }
        }
        if(!isExists) throw new Exception("Playlist does not exist");

        // check if the curr user exists or not
        User newUser = new User();
        boolean isUserExists = false;
        for(User currUser:users){
            String tempUser = currUser.getMobile();
            if(tempUser.equals(mobile)){
                isUserExists = true;
                newUser = currUser;
            }
        }
        if(!isUserExists) throw new Exception("User does not exist");

        List<User> userList = new ArrayList<>();
        if(playlistListenerMap.containsKey(newPlaylist)){
            userList = playlistListenerMap.get(newPlaylist);
        }
        if(!userList.contains(newUser)) userList.add(newUser);
        playlistListenerMap.put(newPlaylist,userList);

        List<Playlist> userPlaylist = new ArrayList<>();
        if(userPlaylistMap.containsKey(newUser)){
            userPlaylist = userPlaylistMap.get(newUser);
        }
        if(!userPlaylist.contains(newPlaylist)) userPlaylist.add(newPlaylist);
        userPlaylistMap.put(newUser,userPlaylist);
        return newPlaylist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        Song newSong = new Song();
        boolean isSongExists = false;
        for(Song currSong : songs){
            String currSongTitle = currSong.getTitle();
            if(currSongTitle.equals(songTitle)){
                newSong = currSong;
                isSongExists = true;
            }
        }
        if(!isSongExists) throw new Exception("Song does not exist");

        User newUser = new User();
        boolean isUserExists = false;
        for (User currUser: users){
            String currUserMobile = currUser.getMobile();
            if(currUserMobile.equals(mobile)){
                newUser = currUser;
                isUserExists = true;
            }
        }
        if(!isUserExists) throw new Exception("User does not exist");

        List<User> userList = new ArrayList<>();
        if(songLikeMap.containsKey(newSong)) userList = songLikeMap.get(newSong);

        if(!users.contains(newUser)){
            users.add(newUser);
            songLikeMap.put(newSong,users);
            newSong.setLikes(newSong.getLikes()+1);

            Album newAlbum = new Album();
            for(Album currAlbum : albumSongMap.keySet()){
                List<Song> temp = albumSongMap.get(currAlbum);
                if(temp.contains(newSong)){
                    newAlbum=currAlbum;
                    break;
                }
            }

            Artist newArtist = new Artist();
            for(Artist currArtist : artistAlbumMap.keySet()){
                List<Album> tempAlbum = artistAlbumMap.get(currArtist);
                if(tempAlbum.contains(newAlbum)){
                    newArtist=currArtist;
                    break;
                }
            }

            int oldLikes = newArtist.getLikes();
            newArtist.setLikes(oldLikes+1);
        }
        return newSong;
    }

    public String mostPopularArtist() {
        String newName="";
        int maximumLikes = Integer.MIN_VALUE;
        for(Artist art : artists){
            maximumLikes= Math.max(maximumLikes,art.getLikes());
        }
        for(Artist currArt : artists){
            if(maximumLikes==currArt.getLikes()){
                newName=currArt.getName();
            }
        }
        return newName;
    }

    public String mostPopularSong() {
        String newName="";
        int maximumLikes = Integer.MIN_VALUE;
        for(Song currSong : songs){
            maximumLikes=Math.max(maximumLikes,currSong.getLikes());
        }
        for(Song currSong : songs){
            if(maximumLikes==currSong.getLikes())
                newName=currSong.getTitle();
        }
        return newName;
    }
}
