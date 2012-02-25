import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.component.LifeCycle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import java.io.IOException;
import java.net.UnknownHostException;

class GridfsHandler extends AbstractHandler {
	
	GridFS fs;
	
	public GridfsHandler() throws UnknownHostException {
		super();
		Mongo mongo = new Mongo("127.0.0.1" , 27017);
		fs = new GridFS(mongo.getDB("test"));
	}
	
	@SuppressWarnings("deprecation")
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {
		String path = request.getPathInfo().substring(1);
		GridFSDBFile file = fs.findOne(path);
		
		if (file == null) {
		    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    response.setContentType("text/plain");
		    response.getWriter().println("Not Found");
		} else {
		    response.setStatus(HttpServletResponse.SC_OK);
		    response.setContentLength((int) file.getLength());
		    response.setContentType(file.getContentType());
		    response.setHeader("Last-Modified", file.getUploadDate().toGMTString());
		    response.setHeader("Etag", file.getId().toString());
		    file.writeTo(response.getOutputStream());
		}
		baseRequest.setHandled(true);
	}
}

class GridfsServer {	
	public static void main(String[] args) throws Exception {
		Server server = new Server(9999);
		server.setHandler(new GridfsHandler());		 
		server.start();
	}
}
