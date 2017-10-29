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
public class RoutingServlet extends HttpServlet {

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException
    {

        Double lat1 = Double.parseDouble(request.getParameter("lat1"));
        Double lng1 = Double.parseDouble(request.getParameter("lng1"));
        Double lat2 = Double.parseDouble(request.getParameter("lat2"));
        Double lng2 = Double.parseDouble(request.getParameter("lng2"));

        //int source = getIndex(lat1,lng1);
        //int target = getIndex(lat2, lng2)

        ServletContext context = getServletContext();
        GraphNetwork.initialiseGraph(context);

        response.getWriter().write(GraphNetwork.getRoute());

    }

}
