import java.sql.*;

public class SQL {
    Connection con;
    PreparedStatement sql;

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expfinal?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai",
                    "user1", "111222333");
            System.out.println("连接成功");
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String name, int score) {
        try {
            System.out.println(name + " " + score);
            String sql = "insert into leaderboard(name, score) values(?, ?)";
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, name);
            pre.setInt(2, score);
            pre.executeUpdate();
            System.out.println("已发送");
        } catch (Exception e) {

        }
    }
}


