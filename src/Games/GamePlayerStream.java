package Games;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by QAQ on 2017/4/2.
 */
public class GamePlayerStream implements IGamePlayer {

    private InputStream inputStream;
    private OutputStream outputStream;
    GamePlayerStream(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public int getInt() throws GameReadException{
        try {
            byte[] b = new byte[4];
            int len = inputStream.read(b);
            if(len!=4){
                throw new GameReadException();
            }
            return (b[0]<<24) + (b[1]<<16)+ (b[2]<<8)+ b[3];
        } catch (IOException e) {
            throw new GameReadException();
        }
    }
    public void putInt(int a) throws GameReadException{
        try {
            byte[] b = new byte[4];
            b[0] = (byte) (a >> 24);
            b[1] = (byte) (a >> 16);
            b[2] = (byte) (a >> 8);
            b[3] = (byte) (a);
            outputStream.write(b);
        }catch (IOException e){
            throw new GameReadException();
        }
    }
}
