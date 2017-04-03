package Games;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by syimlzhu on 2017/1/13.
 */
public class GamePlayerSocket extends GamePlayerStream {

    private Socket socket;
    GamePlayerSocket(Socket socket) throws IOException {
        super(socket.getInputStream(),socket.getOutputStream());
        this.socket = socket;
    }
    public Socket getSocket(){
        return socket;
    }
}
