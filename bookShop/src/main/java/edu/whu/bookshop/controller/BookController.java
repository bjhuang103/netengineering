package edu.whu.bookshop.controller;


import edu.whu.bookshop.dbtool.DataTool;
import edu.whu.mSpring.annotation.*;

@Component
@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired
    UserController userController;

    @Autowired
    DataTool dataTool;

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public Object getBookInfo(@PathParam(name = "id")String id){
        return userController.getUserInfo("1");
    }
}
