package edu.whu.bookshop.controller;

import edu.whu.mSpring.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
}
