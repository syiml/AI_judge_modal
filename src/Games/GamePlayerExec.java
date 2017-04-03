package Games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by QAQ on 2017/4/2.
 */
public class GamePlayerExec implements IGamePlayer {

    Process runExe;
    BufferedReader is;

    public GamePlayerExec(Process runExe){
        this.runExe = runExe;
        is = new BufferedReader(new InputStreamReader(runExe.getInputStream()));
    }

    @Override
    public int getInt() throws GameReadException {
        try {
            long now = System.currentTimeMillis();
            while(!is.ready()) {
                if(System.currentTimeMillis() >= now + 1000){
                    throw new GameReadException();
                }
            }
            String isOut = is.readLine();
            return Integer.parseInt(isOut);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            throw new GameReadException();
        }
    }

    @Override
    public void putInt(int a) throws GameReadException {
        try {
            runExe.getOutputStream().write((a + "\n").getBytes());
            runExe.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GameReadException();
        }
    }
}
