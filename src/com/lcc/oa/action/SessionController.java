package com.lcc.oa.action;

import com.lcc.oa.util.Constants;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;

/**
 * 在线会话管理
 * Created by asus on 2017/4/1.
 */
@RequiresPermissions("session:*")
@Controller
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private SessionDAO sessionDAO;

    @RequestMapping()
    public String list(Model model){
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        model.addAttribute("sessions",sessions);
        model.addAttribute("sessionCount",sessions.size());
        return "session/list";
    }

    @RequestMapping("/{sessionId}/forceLogout")
    public String forceLogout(@PathVariable("sessionId") String sessionId,RedirectAttributes redirectAttributes){
        try{
            Session session = sessionDAO.readSession(sessionId);
            if (session != null){
                session.setAttribute(Constants.SESSION_FORCE_LOGOUT_KEY, Boolean.TRUE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("msg","强制退出成功");
        return "redirect:/sessions";
    }
}
