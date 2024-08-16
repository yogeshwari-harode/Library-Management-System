package Mypackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class View_Cart extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String url = "jdbc:derby://localhost:1527/LMS";
        String uid = "app";
        String pwd = "app";
        String query = "SELECT * FROM book WHERE status = 'addToCart'";
        int n = 1;

        try (PrintWriter out = response.getWriter(); 
             Connection con = DriverManager.getConnection(url, uid, pwd); 
             PreparedStatement ps = con.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {

            out.print("<style>"
                    + ".viewtable { border-collapse: collapse; margin-left:15px; }"
                    + ".viewtable th { background-color: orange; padding: 25px; }"
                    + ".viewtable td { padding: 35px; }"
                    + "img { width: 100px; height: 150px; }"
                    + "button.add-to-cart { padding: 10px 20px; font-size: 14px; border: none; border-radius: 5px; background-color: #4CAF50; color: white; cursor: pointer; transition: background-color 0.3s, transform 0.3s; }"
                    + "button.add-to-cart:hover { padding: 10px 20px; font-size: 14px; border: none; border-radius: 5px; background-color: #45a049; color: white; cursor: pointer; transition: background-color 0.3s, transform 0.3s;}"
                    + "button.download-pdf { margin-top: 10px; background-color: #f44336; }"
                    + "button.download-pdf:hover { background-color: #FF4500; transform: scale(1.05); }"
                    + "</style>");
            out.print("<table class='viewtable' border='2'><tr><th>S.No.</th><th>Title</th><th>Author</th><th>ISBN</th><th>Price</th><th>Category</th><th>Book</th><th>Action</th></tr>");

            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                long isbn = rs.getLong("isbn");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                
                InputStream coverStream = rs.getBinaryStream("bookcover");
                String base64Image = "";
                if (coverStream != null) {
                    base64Image = encodeImageToBase64(coverStream);
                }

                out.print("<tr><td>" + n + "</td><td>"  + title + "</td><td>" + author + "</td><td>" + isbn + "</td><td>" + price + "</td><td>" + category + "</td>");
                if (!base64Image.isEmpty()) {
                    out.print("<td><img src='data:image/jpg;base64," + base64Image + "' /></td>");
                } else {
                    out.print("<td>No Image</td></tr>");
                }
                out.print("<td><form action='RemoveFromCart' method='post'>");
                out.print("<input type='hidden' name='isbn' value='" + isbn + "'/>");
                out.print("<button type='submit' class='add-to-cart'>Remove from Watchlist</button></form>");
                out.print("<form action='Download_Book_PDF' method='post'>");
                out.print("<input type='hidden' name='isbn' value='" + isbn + "'/>"); // Hidden field for ISBN
                out.print("<button style='margin-top:10px' type='submit' class='add-to-cart download-pdf'>Download Book PDF</button>");
                out.print("</form></td></tr>");
                n++;
            }
            out.print("</table>");
           
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private String encodeImageToBase64(InputStream coverStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[1024];
        while ((bytesRead = coverStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        buffer.flush();
        byte[] imageBytes = buffer.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
