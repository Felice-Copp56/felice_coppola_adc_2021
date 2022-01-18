package Interfaces;

import java.io.IOException;

public interface AnonymousChat {
    /**
     * Creates new room.
     * @param _room_name a String the name identify the public chat room.
     * @return true if the room is correctly created, false otherwise.
     */
    public boolean createRoom(String _room_name);
    /**
     * Joins in a public room.
     * @param _room_name the name identify the public chat room.
     * @return true if join success, false otherwise.
     */
    public String joinRoom(String _room_name);
    /**
     * Leaves in a public room.
     * @param _room_name the name identify the public chat room.
     * @return true if leave success, false otherwise.
     */
    public String leaveRoom(String _room_name) throws ClassNotFoundException, IOException;
    /**
     * Sends a string message to all members of a  a public room.
     * @param _room_name the name identify the public chat room.
     * @param _text_message a message String value.
     * @return true if send success, false otherwise.
     */
    public String sendMessage(String _room_name, String _text_message) throws ClassNotFoundException;


    //Nuovi metodi aggiunti per ulteriori funzionalità
    public String leaveNetwork() throws IOException, ClassNotFoundException;

    public String destroyRoom(String _room_name) throws ClassNotFoundException, IOException;

    public String showUsers(String _room_name) throws ClassNotFoundException;


}
