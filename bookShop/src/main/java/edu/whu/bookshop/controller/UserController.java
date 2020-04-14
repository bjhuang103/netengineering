package edu.whu.bookshop.controller;

import edu.whu.mSpring.annotation.*;
import edu.whu.mSpring.servlet.SessionHelper;
import edu.whu.mTomcat.connector.HttpRequest;
import edu.whu.mTomcat.connector.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@Component(name = "userController")
public class UserController {

    @Autowired(name = "bookController")
    BookController bookController;

    @RequestMapping(value = "/info",method = {RequestMethod.GET})
    public Object getUserInfo(@PathParam(name = "id")String id){
        Map user = new HashMap();
        user.put("id","1111");
        user.put("name","user1");
        user.put("psw","123321");
        user.put("introduce","hahahahahhahaha");
        return user;
    }


    private String name = "user1";
    private String psw = "psw1";
    @RequestMapping(value = "/login",method = {RequestMethod.POST})
    public String login(HttpServletResponse response, @RequestBody Map data){
        String name1 = (String)data.get("name");
        String name2 = (String)data.get("psw");
        if(data.get("name") != null && data.get("name").equals(name)
                && data.get("psw") != null && data.get("psw").equals(psw)){
            HttpResponse httpResponse = (HttpResponse) response;
            UUID uuid = UUID.randomUUID();
            SessionHelper.putSession(uuid.toString(),data);
            httpResponse.setSeesion(uuid.toString());
            return "login success";
        } else {
            return "login failed";
        }
    }

    @RequestMapping(value = "/checklogin",method = {RequestMethod.GET})
    public Object hasLogin(HttpServletRequest request){
        HttpRequest httpRequest = (HttpRequest) request;
        String sessionid = httpRequest.getRequestedSessionId();
        if(SessionHelper.getSession(sessionid) != null){
            return SessionHelper.getSession(sessionid);
        } else {
            return "not login!";
        }
    }
}
