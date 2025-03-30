import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8848)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            System.out.println("客户端连接: " + clientSocket.getRemoteSocketAddress());

            String inputLine;
            String userId = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("收到客户端消息: " + inputLine);
                Message meg = SerializeUtil.deserialize(inputLine);
                String operation = meg.getOperation();
                if (operation.equals(Constant.HELO)) {
                    // 用户登录请求
                    // get id
                    userId = meg.getText();
                    Object o = JDBCUtil.execSQL("select count(*) from user where user_id = " + userId);
                    if (o != null) {
                        ResultSet resultSet = (ResultSet) o;
                        if (resultSet.next()) { // 移动到第一行
                            if (resultSet.getInt(1) == 1) {
                                // 用户存在
                                out.println(Constant.ASK_PASS);
                            } else {
                                // 用户不存在
                                out.println(Constant.LOG_FAIL);
                            }
                        }
                    }
                }else if (operation.equals(Constant.PASS)) {
                    String password = meg.getText();
                    Object o = JDBCUtil.execSQL("select count(*) from user where user_id = " + userId + " and password = " + password);
                    if (o != null) {
                        ResultSet resultSet = (ResultSet) o;
                        if (resultSet.next()) { // 移动到第一行
                            if (resultSet.getInt(1) == 1) {
                                out.println(Constant.LOG_OK);
                            } else {
                                out.println(Constant.LOG_FAIL);
                            }
                        }
                    }
                }else if (operation.equals(Constant.BALA)){
                    //查询余额
                    Object o = JDBCUtil.execSQL("select balance from user where user_id = " + userId);
                    if(o != null){
                        ResultSet resultSet = (ResultSet) o;
                        if(resultSet.next()){
                            out.println(Constant.AMNT + " " + resultSet.getInt(1));
                        }
                    }
                }else if(operation.equals(Constant.WDRA)){
                    //取款
                    int amount = Integer.parseInt(meg.getText());
                    //查询余额
                    Object o = JDBCUtil.execSQL("select balance from user where user_id = " + userId);
                    if(o != null){
                        ResultSet resultSet = (ResultSet) o;
                        if(resultSet.next()){
                            if(resultSet.getInt(1) >= amount){
                                int newBalance = resultSet.getInt(1) - amount;
                                JDBCUtil.execSQL("update user set balance = " + newBalance + " where user_id = " + userId);
                                out.println(Constant.LOG_OK);
                            }else{
                                out.println(Constant.LOG_FAIL);
                            }
                        }
                    }
                }else if(operation.equals(Constant.BYE)){
                    System.out.println("客户端断开连接: " + clientSocket.getRemoteSocketAddress());
                    out.println(Constant.BYE);
                    clientSocket.close();
                    return;
                }else{
                    //异常指令
                    out.println(Constant.LOG_FAIL);
                }

            }

        } catch (IOException e) {
            System.out.println("客户端连接异常: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
