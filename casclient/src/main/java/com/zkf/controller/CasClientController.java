package com.zkf.controller;

import com.zkf.repository.InfoRepository;
import com.zkf.repository.STRepository;
import com.zkf.vo.Info;
import com.zkf.vo.ST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/")
public class CasClientController {
    private static final Logger logger = LoggerFactory.getLogger(CasClientController.class);

    @Autowired
    STRepository stRepository;

    @Autowired
    InfoRepository infoRepository;

    @Value("${cas_server_url}")
    private String cas_server_url;

    /**
     * 用户登录
     *
     * @return
     */
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String hello() {
        return "Hello,this is cas client";
    }

    @RequestMapping(value = "/client/index", method = {RequestMethod.GET, RequestMethod.POST})
    public String  index(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String url = "";
        String username = request.getParameter("username") == null ? "" : request.getParameter("username");
        String ticket = request.getParameter("ticket") == null ? "" : request.getParameter("ticket");
        //如果ticket不为空，则登录，否则转向casServer的login
        if (!username.isEmpty() & !ticket.isEmpty()) {
            ST st = stRepository.findByStAndValidate(ticket, 1);
            if (st != null) {
                Info info = infoRepository.findByUsername(username);
//                url = "index.html" + "?" + "username=" + info.getUsername() + "&info=" + info.getInfo();
//                url = "index.html" + "?" + "username=" + info.getUsername() + "&ticket=" + ticket;
//                url = "index";
                request.setAttribute("username", info.getUsername());
                request.setAttribute("ticket", ticket);
                url = "index";

            }
        } else {
            //在 CAS认证中心登录
            String cas_service_url = "http://127.0.0.1:8001/client/service";
            url = "redirect:" + cas_server_url + "/server/login?" + "service=" + cas_service_url;
//            response.sendRedirect(url);
        }
        return url;
//        request.getRequestDispatcher("/index.html").forward(request, response);
    }

    /**
     * 接收浏览器发来的ST，创建ST记录，转向casServer认证ST
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/client/service", method = {RequestMethod.GET, RequestMethod.POST})
    public String service(HttpServletRequest request, HttpServletResponse response) {
        String service_ticket = request.getParameter("ticket");
        ST st = new ST(service_ticket);
        st.setValidate(1);
        stRepository.save(st);

        String url = "";
        String cas_service_url = "http://127.0.0.1:8001/client/service";
        url = cas_server_url + "/server/validate?" + "ticket=" + service_ticket + "&service=" + cas_service_url;
        return "redirect:" + url;
    }

    @RequestMapping(value = "/client/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpServletRequest request,
                         HttpServletResponse response,
                         RedirectAttributes attributes,
                         @RequestParam String username,
                         @RequestParam String ticket
    ) {
        String url = "";
        String cas_service_url = "http://127.0.0.1:8001/client/service";
        url = cas_server_url + "/server/logout?" + "service=" + cas_service_url;
        Map<String, String[]> parameterMap = request.getParameterMap();
        attributes.addAttribute("username", username);
        attributes.addAttribute("ticket", ticket);
//        这里如果使用addFlashAttribute，无法取值
//        attributes.addFlashAttribute("ticket", ticket);
//        attributes.addFlashAttribute("username", username);
        return "redirect:" + url;
    }
}
