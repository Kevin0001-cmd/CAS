package com.zkf.controller;

import com.zkf.repository.STRepository;
import com.zkf.repository.ServiceRepository;
import com.zkf.repository.TGTRepository;
import com.zkf.repository.UserRepository;
import com.zkf.vo.ST;
import com.zkf.vo.Service;
import com.zkf.vo.TGT;
import com.zkf.vo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class CasServerController {
    private static final Logger logger = LoggerFactory.getLogger(CasServerController.class);

    @Autowired
    TGTRepository tgtRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    STRepository stRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${cas_service_url}")
    private String cas_service_url;

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String hello_world() {
        return "CAS Server!";
    }

    @RequestMapping(value = "/server/login", method = {RequestMethod.GET, RequestMethod.POST})
//    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //在认证中心登录
        /**
         * 1. 客户端请求cas服务器，服务器判断是GET还是POST请求
         *      GET
         *          1. 服务器获取cookie中的CASTGC
         *          2. 若CASTGC不为空，则根据TGC的id和validate查找是否存在记录
         *          3. 如果查找到了，则证明用户已经登录，生成ST，派发给客户端，否则跳转登录页面，让用户登录
         *      POST
         *          用户登录
         *          1. 根据用户名和密码查找用户
         *          2. 生成tgc和st，转向服务地址
         */
        if ("GET".equals(request.getMethod())) {
            Cookie[] cookies = request.getCookies();
            String castgc = "";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("CASTGC".equals(cookie.getName())) {
                        castgc = cookie.getValue();
                        logger.info("castgc:", castgc);
                    }
                }
            }
            if (!castgc.isEmpty()) {
                List tgts = tgtRepository.findByIdAndValidate(Integer.valueOf(castgc), 1);
                if (tgts.size() > 0) {
                    TGT tgt = (TGT) tgts.get(0);
                    if (tgt != null) {
                        // TGT存在，证明 用户已登录
                        // 签发 service_ticket
                        String url = create_st(request, tgt.getUser_id(), String.valueOf(tgt.getId()));
                        if (!url.isEmpty()) {
                            response.sendRedirect(url);
                        } else {
                            return "no service & login success";
                        }
                    }
                }
            }
//            request.getRequestDispatcher("login").forward(request, response);
            return "login";
        } else {
            //输入每用户了和密码 登录
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            User user = userRepository.findByUsernameAndPassword(username, password);
            if (user != null) {
                //TGT不存在，用户未登录
                //生成TGC，ticket_granted_ticket
                String tgc = create_tgt(user.getId());
                logger.info("tgc:", tgc);
                String url = create_st(request, user.getId(), tgc);
                if (!url.isEmpty()) {
                    //重定向到client
                    //保存TGC到cookie
                    return "redirect:" + url;
                } else {
                    return "no service & login success";
                }
            } else {
                return "用户名或者密码错误";
            }
        }
    }


    /**
     * 验证ST
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/server/validate", method = {RequestMethod.GET, RequestMethod.POST})
    public String service_validate(HttpServletRequest request, HttpServletResponse response) {
        String url = "";
        String service_ticket = request.getParameter("ticket");
        String service_url = request.getParameter("service");
        Service service = serviceRepository.findByUrl(service_url);
        ST st = stRepository.findByStAndServiceIdAndUsedAndValidate(service_ticket,
                service.getId(), 0, 1);
        if (st != null) {
            User user = userRepository.findUserById(Integer.valueOf(st.getUserId()));
            int used = st.getUsed();
            st.setUsed(used + 1);
            stRepository.save(st);
            String username = user.getUsername();
            url = "redirect:" + cas_service_url + "/client/index" + "?username=" + username + "&ticket=" + service_ticket;
        }
        return url;
    }

    /**
     * 根据service_url和cookie里面的tgc，查找casServer中的tgt和st,将st设置为无效
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/server/logout", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String logout(HttpServletRequest request, HttpServletResponse response,
                         @ModelAttribute("username") String username, @ModelAttribute("ticket") String ticket) {
//        String service_url = request.getParameter("service");
//        Cookie[] cookies = request.getCookies();
//        String castgc = "";
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("CASTGC".equals(cookie.getName())) {
//                    castgc = cookie.getValue();
//                    logger.info("castgc:", castgc);
//                }
//            }
//        }
//        TGT tgt = tgtRepository.findById(castgc);
//        ST st = stRepository.findByTgtIdAndValidate(tgt.getId(), 1);
        ST st = stRepository.findByStAndValidate(ticket,1);
        st.setValidate(-1);
        stRepository.save(st);
        return "CAS server logout";
    }

    public String create_tgt(String user_id) {
        TGT tgt = new TGT(UUID.randomUUID().toString(), user_id);
        tgtRepository.save(tgt);
        return String.valueOf(tgt.getId());
    }

    /**
     * 生成 ST
     *
     * @param request
     * @param user_id
     * @param tgt_id
     * @return
     */
    public String create_st(HttpServletRequest request, String user_id, String tgt_id) {
        String url = "";
        String service_url = "";
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {
            if ("service".equals(key)) {
                String[] value = parameterMap.get(key);
                service_url = value[0];
            }
        }
//        String service_url = getOneParameter(request.getRequestURL().toString(), "service");
        //验证service是否已注册
        Service service = serviceRepository.findByUrl(service_url);
        if (!service_url.isEmpty() & service != null) {
            ST st_model = new ST(UUID.randomUUID().toString(), user_id, tgt_id, service_url, service.getId().toString(), 0);
            st_model.setValidate(1);
            stRepository.save(st_model);
            url = service_url + "?" + "ticket=" + st_model.getSt();
        }
        return url;
    }

    public String getOneParameter(String url, String keyWord) {
        String retValue = "";
        try {
            final String charset = "utf-8";
            url = URLDecoder.decode(url, charset);
            if (url.indexOf('?') != -1) {
                final String contents = url.substring(url.indexOf('?') + 1);
                String[] keyValues = contents.split("&");
                for (int i = 0; i < keyValues.length; i++) {
                    String key = keyValues[i].substring(0, keyValues[i].indexOf("="));
                    String value = keyValues[i].substring(keyValues[i].indexOf("=") + 1);
                    if (key.equals(keyWord)) {
                        if (value != null || !"".equals(value.trim())) {
                            retValue = value;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retValue;
    }
}
