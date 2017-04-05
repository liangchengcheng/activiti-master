package com.lcc.oa.action;

import com.lcc.oa.entity.Resource;
import com.lcc.oa.pagination.Pagination;
import com.lcc.oa.pagination.PaginationThreadUtils;
import com.lcc.oa.service.IResourceService;
import com.lcc.oa.util.Constants;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiresPermissions("admin:*")
@RequestMapping(value = "/resourceAction")
public class ResourceAction {

    @Autowired
    protected IResourceService resourceService;

    @RequestMapping(value = "/listResource_page")
    public String listResource_page(Model model) throws Exception {
        List<Resource> list = this.resourceService.getResourceListPage();
        Pagination pagination = PaginationThreadUtils.get();
        model.addAttribute("list", list);
        model.addAttribute("page", pagination.getPageStr());
        return "resource/list_resource";
    }

    @RequestMapping(value = "/toAdd")
    public String toAdd(Model model) throws Exception {
        if (!model.containsAttribute("res")) {
            model.addAttribute("res", new Resource());
        }
        List<Resource> menuList = this.resourceService.getResourceByType();
        model.addAttribute("list", menuList);
        return "resource/add_resource";
    }

    /**
     * 此方法会在其他方法之前执行，并将其自动添加到模型对象中，
     * 在功能处理方法中调用Model 入参的containsAttribute("list")将会返回true。
     */

    //	@ModelAttribute("list")
    //	public List<Resource> getResource() throws Exception{
    //		return this.resourceService.getResourceByType();
    //	}
    @RequestMapping(value = "/doAdd")
    public String doAdd(@Valid @ModelAttribute("res") Resource res,
                        BindingResult results, Model model) throws Exception {
        if (results.hasErrors()) {
            return toAdd(model);
        }

        res.setAvailable(1);
        this.resourceService.doAdd(res);
        return "redirect:/resourceAction/listResource_page";
    }

    @RequestMapping(value = "/toUpdate/{id}")
    public String toUpdate(@PathVariable("id") Integer id, Model model) throws Exception {
        if (!model.containsAttribute("resource")) {
            if (id != null) {
                Resource resource = this.resourceService.getPermissions(id);
                model.addAttribute("resource", resource);
            } else {
                model.addAttribute(Constants.MESSAGE, "出错，id为空！");
                return toAdd(model);
            }
        }
        List<Resource> menuList = this.resourceService.getResourceByType();
        model.addAttribute("list", menuList);
        return "resource/update_resource";
    }

    @RequestMapping(value = "/doUpdate")
    public String doUpdate(@Valid Resource resource, BindingResult results,
                           Model model, RedirectAttributes redirectAttribute) throws Exception {
        if (results.hasErrors()) {
            return toUpdate(resource.getId(), model);
        }
        try {
            this.resourceService.doUpdate(resource);
            redirectAttribute.addFlashAttribute(Constants.MESSAGE, "修改成功！");
        } catch (Exception e) {
            redirectAttribute.addFlashAttribute(Constants.MESSAGE, "修改失败！");
            throw e;
        }
        return "redirect:/resourceAction/toUpdate/" + resource.getId();
    }

    @RequestMapping(value = "/doDelete/{id}")
    public String doDelete(@PathVariable("id") Integer id, RedirectAttributes redirectAttribute) throws Exception {
        try {
            Resource resource = this.resourceService.getPermissions(id);
            resource.setAvailable(0);
            this.resourceService.doUpdate(resource);
            redirectAttribute.addFlashAttribute(Constants.MESSAGE, "删除成功！");
        } catch (Exception e) {
            redirectAttribute.addFlashAttribute(Constants.MESSAGE, "删除失败！");
            throw e;
        }
        return "redirect:/resourceAction/listResource_page";
    }
}
