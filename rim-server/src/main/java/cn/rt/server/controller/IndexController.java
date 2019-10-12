package cn.rt.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ruanting
 * @date 2019/10/12
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping(value = "/testIndex")
    @ResponseBody
    public String testIndex() {
        return "index success";
    }

}
