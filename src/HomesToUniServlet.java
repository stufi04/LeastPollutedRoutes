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
public class HomesToUniServlet extends HttpServlet {

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException
    {

        String str = request.getParameter("list");

        response.getWriter().write(GraphNetwork.getRoutesFromHomesToUniAndAirspeckPositions(str));

    }

}
