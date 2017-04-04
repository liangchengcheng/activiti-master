package com.lcc.oa.action;

import java.util.List;
import javax.servlet.http.HttpSession;

import com.lcc.oa.entity.GroupAndResource;
import com.lcc.oa.entity.Resource;
import com.lcc.oa.entity.User;
import com.lcc.oa.service.IGroupAndResourceService;
import com.lcc.oa.service.IResourceService;
import com.lcc.oa.service.IUserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页控制器
 */
@Controller
public class MainController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IGroupAndResourceService grService;

    @Autowired
    private IResourceService resourceService;

    @RequestMapping(value = "/top")
    public String index() {
        return "main/top";
    }

    @RequestMapping(value = "/welcome")
    public String welcome() {
        return "main/welcome";
    }

    @RequestMapping(value = "/nav")
    public String nav(HttpSession session, Model model) throws Exception {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = this.userService.getUserByName(username);
        List<GroupAndResource> grList = this.grService.getResource(user.getGroup().getId());
        List<Resource> menus = this.resourceService.getMenus(grList);
        model.addAttribute("menuList", menus);
        return "main/nav";
    }

    @RequestMapping("/")
    public String index(Model model) throws Exception {
        return "index";
    }
}