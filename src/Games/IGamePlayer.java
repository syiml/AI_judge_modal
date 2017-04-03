package Games;

/**
 * Created by syimlzhu on 2017/1/13.
 */
public interface IGamePlayer {
    int getInt() throws GameReadException;
    void putInt(int a) throws GameReadException;
}
