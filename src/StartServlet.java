import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by st.ivanov44 on 25/10/2017.
 */
public class StartServlet extends HttpServlet {

    public void doGet(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException
    {
        ServletContext context = getServletContext();
        GraphNetwork.initialiseGraph(context);
    }

}
