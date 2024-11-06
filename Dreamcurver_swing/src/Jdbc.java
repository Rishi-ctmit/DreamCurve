import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Jdbc {
    public static void main(String[] args) throws Exception{
        String sql="select name from users where id=1";
        String url="jdbc:postgresql://localhost:5432/postgres";
        String username="postgres";
        String pass="rishi@123";

        Connection con= DriverManager.getConnection(url,username,pass);

        Statement st=con.createStatement();
        ResultSet rs= st.executeQuery(sql);
        rs.next();
        String result=rs.getString(1);
        System.out.println(result);
    }
}
