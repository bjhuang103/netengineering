package edu.whu.mSpring.servlet;


import com.alibaba.fastjson.JSON;
import edu.whu.mSpring.SpringApplication;
import edu.whu.mSpring.annotation.*;
import edu.whu.mTomcat.connector.HttpRequest;
import edu.whu.mTomcat.connector.HttpResponse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    private List<String> classNames = new ArrayList<>();

//    private Map<String, Object> ioc = new HashMap<>();
//
//    private Map<String, Method> handlerMapping = new  HashMap<>();
//
//    private Map<String, Object> controllerMap  =new HashMap<>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //处理请求
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500!! Server Exception");
        }

    }

    private RequestMethod convertMethod(String method){
        switch (method){
            case "GET":
                return RequestMethod.GET;
            case "POST":
                return RequestMethod.POST;
            case "PUT":
                return RequestMethod.PUT;
            case "DELETE":
                return RequestMethod.DELETE;
            case "HEAD":
                return RequestMethod.HEAD;
            default:
                return RequestMethod.HEAD;
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url =req.getRequestURI();
        String contextPath = req.getContextPath();

        //拼接url并把多个/替换成一个
        url=url.replace(contextPath, "").replaceAll("/+", "/");

        if(!SpringApplication.handlerMappingMethod.containsKey(url)
                || !SpringApplication.handlerMappingMethod.get(url).containsKey(convertMethod(req.getMethod()))){
            // TODO send error msg
//            resp.getWriter().write("404 NOT FOUND!");
            resp.sendError(404);
            resp.getWriter().print("\r\n");
            return;
        }

        Method method =SpringApplication.handlerMappingMethod.get(url).get(convertMethod(req.getMethod()));

        //获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        //获取请求的参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        //保存参数值
        Object [] paramValues= new Object[parameterTypes.length];

        //方法的参数列表
        for (int i = 0; i<parameterTypes.length; i++){
            //根据参数名称，做某些处理
            String requestParam = parameterTypes[i].getSimpleName();


            if (requestParam.equals("HttpServletRequest")){
                //参数类型已明确，这边强转类型
                paramValues[i]=req;
            }
            else if (requestParam.equals("HttpServletResponse")){
                paramValues[i]=resp;
            }
            else if(requestParam.equals("String") && parameterAnnotations[i][0] instanceof PathParam){
                PathParam pathParam = (PathParam) parameterAnnotations[i][0];
                paramValues[i] = req.getParameter(pathParam.name());
            } else if (requestParam.equals("Map") && parameterAnnotations[i][0] instanceof RequestBody){
                paramValues[i] = ((HttpRequest)req).getBody();
            }
        }
        Object result = null;
        //利用反射机制来调用
        try {
            result = method.invoke(SpringApplication.handlerMappingController.get(url), paramValues);//obj是method所对应的实例 在ioc容器中
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpResponse httpResponse = (HttpResponse)resp;
        httpResponse.sendHeaders();
        if(result == null) return;
        PrintWriter writer = httpResponse.getWriter();
        if(result.getClass() == String.class){
            writer.println(result);
        } else {
            writer.println(JSON.toJSONString(result));
        }
        writer.print("\r\n");
        writer.flush();
    }



    private void  doLoadConfig(String location){
        //把web.xml中的contextConfigLocation对应value值的文件加载到留里面
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            //用Properties文件加载文件里的内容
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关流
            if(null!=resourceAsStream){
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
