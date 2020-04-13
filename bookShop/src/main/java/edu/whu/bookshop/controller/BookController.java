package edu.whu.bookshop.controller;


import edu.whu.mSpring.annotation.*;

@Component(name = "bookController")
@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired(name = "userController")
    UserController userController;

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public Object getBookInfo(@PathParam(name = "id")String id){
        return userController.getUserInfo("1");
    }
}
