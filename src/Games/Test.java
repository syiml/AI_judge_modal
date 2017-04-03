package Games;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 1表示黑子，2表示白子
 * 黑子先下
 * Created by syimlzhu on 2016/9/30.
 */
class GameGoBang extends BaseGame{
    private int row;
    private int col;
    private int chessBoard[][];
    /**
     * 当前落子方  用1、2表示
     */
    private int nowPlayer;
    private JSONObject record;
    private int stepNum;
    private int resultStatus = -2;

    public GameGoBang(int row,int col,IGamePlayer p1,IGamePlayer p2,String p1_name,String p2_name){
        this.row = row;
        this.col = col;
        chessBoard = new int[row][];
        for(int i=0;i<row;i++){
            chessBoard[i]=new int[col];
            for(int j=0;j<col;j++){
                chessBoard[i][j] = 0;
            }
        }
        record = new JSONObject();
        record.put("type","GoBang");
        record.put("row",row);
        record.put("col",col);
        record.put("step",new JSONArray());
        record.put("black",p1_name);
        record.put("white",p2_name);
        stepNum = 0;
        nowPlayer = 1;
        players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
    }

    /**
     * 结束状态意义：
     * -1:游戏还未开始，告诉先后手时失败
     *  0:平局
     *  1:player1胜
     *  2:player2胜
     *  3:player1胜，原因是player2读取/发送数据失败、落子到非法位置
     *  4:player2胜，原因是player1读取/发送数据失败、落子到非法位置
     * @param status 状态
     * @return 状态
     */
    @Override
    public int gameEnd(int status){
        //Tool.debug("GameEnd("+status+")");
        JSONObject aStep = new JSONObject();
        aStep.put("type","end");
        aStep.put("status",status);
        record.getJSONArray("step").add(aStep);
        record.put("result",status);
        resultStatus = status;
        return status;
    }

    public int getResultStatus() {
        return resultStatus;
    }

    @Override
    public void run() {
        try {
            players.get(0).putInt(row);
            players.get(0).putInt(col);
            players.get(0).putInt(1);
        }catch (GameReadException e) {
            gameEnd(4);
            return ;
        }

        try {
            players.get(1).putInt(row);
            players.get(1).putInt(col);
            players.get(1).putInt(2);
        }catch (GameReadException e) {
            gameEnd(3);
            return ;
        }

        while(true){
            int x,y,otherPlayer;
            try {
                x = players.get(nowPlayer - 1).getInt();
                y = players.get(nowPlayer - 1).getInt();

                //Tool.debug(nowPlayer+" ("+x+","+y+")");
                otherPlayer = 3 - nowPlayer;
            }catch (GameReadException e){
                gameEnd(nowPlayer + 2);
                e.printStackTrace();
                return ;
            }
            int ret = set(x, y);
            if(ret == 3 || ret == 4) {
                //落子非法
                gameEnd(ret);
                return ;
            }

            try {
                players.get(otherPlayer -1).putInt(x);
                players.get(otherPlayer -1).putInt(y);
            } catch (GameReadException e) {
                gameEnd(otherPlayer + 2);
                return ;
            }
            if(ret != 0){
                if(ret >= 1){
                    gameEnd(nowPlayer);
                }else if(ret == -1){
                    gameEnd(0);
                }
                return ;
            }
            nowPlayer = 3 - nowPlayer;
        }
    }

    /**
     * 在位置i,j下一枚子
     * 返回胜利方
     * 如果当前方下在非法位置，直接判负
     * @param i 位置坐标i
     * @param j 位置坐标j
     * @return 0未分胜负，可以继续
     *         1黑方五连珠胜
     *         2白方五连珠胜
     *         3白方下子非法，黑方胜
     *         4黑方下子非法，白方胜
     *         -1平局
     */
    private int set(int i,int j){
        Tool.debug("set "+i+","+j);
        JSONObject aStep = new JSONObject();
        aStep.put("player",nowPlayer);
        aStep.put("x",i);
        aStep.put("y",j);
        stepNum++;
        if(i<0||i>=row||j<0||j>=col||chessBoard[i][j] != 0){
            aStep.put("type","fail");
            record.getJSONArray("step").add(aStep);
            if(nowPlayer == 1){
                return 4;
            }else{
                return 3;
            }
        }
        chessBoard[i][j] = nowPlayer;

        aStep.put("type","success");
        record.getJSONArray("step").add(aStep);
        return getVec(i,j);
    }

    /**
     * 判断刚刚落的子是否连成五颗
     * @param x 刚刚落完的子所在的x坐标 (从0开始)
     * @param y 刚刚落完的子所在的y坐标 (从0开始)
     * @return 1 连成5个
     *         0 未连成5个
     *         -1 平局(整个棋盘都已经下满)
     */
    private int getVec(int x,int y){
        int player = chessBoard[x][y];
        int next[][] = {
                {0,1},
                {1,1},
                {1,0},
                {1,-1}
        };
        for (int[] aNext : next) {
            int nowLength = 1;
            int nx = x, ny = y;
            while (true) {
                nx += aNext[0];
                ny += aNext[1];
                if (nx < 0 || nx >= row || ny < 0 || ny >= col || chessBoard[nx][ny] != player) {
                    break;
                } else {
                    nowLength++;
                }
            }
            nx = x;
            ny = y;
            while (true) {
                nx -= aNext[0];
                ny -= aNext[1];
                if (nx < 0 || nx >= row || ny < 0 || ny >= col || chessBoard[nx][ny] != player) {
                    break;
                } else {
                    nowLength++;
                }
            }
            Tool.debug("nowlen="+nowLength);
            if(nowLength>=5) return player;
        }
        if(stepNum == row*col){
            return -1;
        }
        return 0;
    }

    public JSONObject getRecord(){
        return record;
    }
}

public class Test {
    public static void main(String[] args) {/*
        JSONObject config = Tool.readJsonFromFile("/data/judge/config.json");
        int total_score = 0;
        int score = 0;
        JSONObject result_json = new JSONObject();
        result_json.put("record",new JSONArray());
        for(int i=0;i<config.getJSONArray("game").size();i++){
            JSONObject games = config.getJSONArray("game").getJSONObject(i);
            int number = games.getInt("number");
            while(number --> 0 ){
                try {
                    final Process runExe_ai = Runtime.getRuntime().exec("/data/ai");
                    final Process runExe_main = Runtime.getRuntime().exec("/data/main");//E:\\syiml\\ACM\\wzq.exe
                    IGamePlayer player1,player2;
                    String p1_name,p2_name;
                    if(games.getBoolean("black")){
                        player1 = new GamePlayerExec(runExe_main);
                        player2 = new GamePlayerExec(runExe_ai);
                        p1_name = "擂主";
                        p2_name = "挑战者";
                    }else{
                        player1 = new GamePlayerExec(runExe_ai);
                        player2 = new GamePlayerExec(runExe_main);
                        p1_name = "挑战者";
                        p2_name = "擂主";
                    }
                    GameGoBang game = new GameGoBang(games.getInt("row"),games.getInt("col"),player1,player2,p1_name,p2_name);
                    total_score += games.getJSONArray("score").getInt(0);
                    game.run();
                    int result = game.getResultStatus();
                    if(result == 0){
                        score += games.getJSONArray("score").getInt(1);
                    }else if(result == 1 || result == 3){//黑方胜
                        if(games.getBoolean("black")){
                            score += games.getJSONArray("score").getInt(0);
                        }else{
                            score += games.getJSONArray("score").getInt(2);
                        }
                    }else if(result == 2 || result == 4){//白方胜
                        if(games.getBoolean("black")){
                            score += games.getJSONArray("score").getInt(2);
                        }else{
                            score += games.getJSONArray("score").getInt(0);
                        }
                    }else{
                        result_json.put("type","fail");
                    }
                    result_json.getJSONArray("record").add(game.getRecord());

                    runExe_ai.destroy();
                    runExe_main.destroy();
                }catch (IOException e){
                    //Tool.log("运行失败");
                }
            }
        }
        if(!result_json.containsKey("type")) {
            result_json.put("type", "success");
        }
        result_json.put("score",score*100/total_score);
        System.out.print(result_json.toString());
        /*
        if(args.length <= 0) {
            Tool.log("运行参数过少，运行失败");
            return ;
        }
        switch (args[0])
        {
            case "GoBang":
            {
                if(args.length<5)
                {
                    Tool.log("运行参数过少，运行失败。GoBang由参数2和3指定棋盘的行和列");
                    return;
                }
                int row = Integer.parseInt(args[1]);
                int col = Integer.parseInt(args[2]);
                IGamePlayer player1,player2;
                //Tool.debug("row="+row+" col="+col);
                try {
                    final Process runExe1 = Runtime.getRuntime().exec(args[3]);
                    final Process runExe2 = Runtime.getRuntime().exec(args[4]);//E:\\syiml\\ACM\\wzq.exe
                    player1 = new GamePlayerExec(runExe1);
                    player2 = new GamePlayerExec(runExe2);
                    GameGoBang game= new GameGoBang(row,col,player1,player2);
                    game.run();
                    System.out.println(game.getRecord().toString());
                    runExe1.destroy();
                    runExe2.destroy();
                } catch (IOException e) {
                    Tool.log("运行失败。");
                    e.printStackTrace();
                    return ;
                }
            }
        }
	    // write your code here
	    */
    }
}
