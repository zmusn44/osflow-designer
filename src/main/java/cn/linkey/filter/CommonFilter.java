package cn.linkey.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.flowchart.api.FlowchartImp;
import cn.linkey.orm.factory.BeanCtx;
import cn.linkey.orm.util.JdbcUtil;
import cn.linkey.util.Tools;

public class CommonFilter implements Filter {

    private static String driver="";
    private static String url="";
    private static String username="";
    private static String password="";
//    private static String url="jdbc:mysql://111.231.102.91:3306/osbpm_12_0?useUnicode=yes&useSSL=false&amp;characterEncoding=UTF8&amp;serverTimezone = GMT";
//    private static String username="root";
//    private static String password="linkey2020";

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        readProperty();
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        Map<String, String[]> map = httpServletRequest.getParameterMap();
        String function = null;
        String processid = null;
        String nodeid = null;
        String extNodeType = null;
        String unid = null;
        String docUnidList = null;
        String nodeList = null;
        String action = null;
        String nodeType = null;
        String startNodeid = null;
        String endNodeid = null;
        String searchStr = null;

        // 流程表单字段配置
        String formConfig = "";

        // 节点按钮配置
        String nodeButtonConfig = "";

        JSONObject reslutJson = new JSONObject();
        JSONArray reslutJsonArr = new JSONArray();
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray newRow = new JSONArray();
        JSONArray editRow = new JSONArray();
        JSONArray delRow = new JSONArray();
        FlowchartImp imp = new FlowchartImp();
        boolean flag = false;
        JdbcUtil jdbc = new JdbcUtil();
        BeanCtx.setConnection(jdbc.getConnection(driver, url, username, password));
        BeanCtx.init(httpServletRequest, httpServletResponse);
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("wf_num")) {
                function = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("Processid")) {
                processid = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("Nodeid")) {
                nodeid = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("ExtNodeType")) {
                extNodeType = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("docUnidList")) {
                docUnidList = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("wf_docunid")) {
                unid = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("nodeList")) {
                nodeList = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("Action")) {
                action = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("NodeType")) {
                nodeType = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("StartNodeid")) {
                startNodeid = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("EndNodeid")) {
                endNodeid = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("searchStr")) {
                searchStr = entry.getValue()[0];
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("json")) {
                json = JSONObject.parseObject(entry.getValue()[0]);
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("jsonArray")) {
                jsonArray = JSONArray.parseArray(entry.getValue()[0]);
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("delRow")) {
                delRow = JSONArray.parseArray(entry.getValue()[0]);
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("newRow")) {
                newRow = JSONArray.parseArray(entry.getValue()[0]);
            } else if (Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("editRow")) {
                editRow = JSONArray.parseArray(entry.getValue()[0]);
            }

            // 表单字段配置
            else if(Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("formConfig")){
                formConfig = entry.getValue()[0];
            }


            // 节点操作按钮配置
            else if(Tools.isNotBlank(entry.getKey()) && entry.getKey().equals("nodeButtonConfig")){
                nodeButtonConfig = entry.getValue()[0];
            }

        }
        if (Tools.isNotBlank(function)) {
            if (function.equals("saveFlowChartGraphic")) {
                reslutJson = imp.saveFlowChartGraphic(processid, json.toJSONString());
            }
            if (function.equals("getFlowChartGraphic")) {
                reslutJson = imp.getFlowChartGraphic(processid);
            }
            if (function.equals("saveFlowChartModByNodeType")) {
                reslutJson = imp.saveFlowChartModByNodeType(processid, nodeid, extNodeType, json);
            }
            if (function.equals("getFlowChartModByNodeType")) {
                reslutJson = imp.getFlowChartModByNodeType(processid, nodeid);
            }
            if (function.equals("saveFlowChartEventByNodeType")) {
                if(newRow.size()>0) {
                    reslutJson.put("save", imp.saveFlowChartEventByNodeType(processid, nodeid, newRow));
                }
                if(editRow.size()>0) {
                    reslutJson.put("edit", imp.saveFlowChartEventByNodeType(processid, nodeid, editRow));
                }
                docUnidList = "";
                for (int i = 0; i < delRow.size(); i++) {
                    if (i > 0) {
                        docUnidList += "," + delRow.getJSONObject(i).get("WF_OrUnid");
                        continue;
                    }
                    docUnidList += delRow.getJSONObject(i).get("WF_OrUnid");
                }
                if(Tools.isNotBlank(docUnidList)) {
                    reslutJson.put("delete", imp.deleteFlowChartEventByNodeType(processid, nodeid, docUnidList));
                }
            }
            if (function.equals("getFlowChartEventByNodeType")) {
                reslutJson = imp.getFlowChartEventByNodeType(processid, nodeid);
            }
            if (function.equals("saveFlowChartMailConfigByNodeType")) {
                reslutJson = imp.saveFlowChartMailConfigByNodeType(processid, nodeid, json);
            }
            if(function.equals("deleteFlowChartMailConfigByNodeType")) {
                imp.deleteFlowChartMailConfigByNodeType(processid, nodeid, docUnidList);
            }
            if (function.equals("getFlowChartMailConfigByNodeType")) {
                reslutJson = imp.getFlowChartMailConfigByNodeType(processid, nodeid);
            }
            if (function.equals("getFlowChartMailConfigByUnid")) {
                reslutJson = imp.getFlowChartMailConfigByUnid(unid);
            }
            if (function.equals("actionFlowChartGraphic")) {
                reslutJson = imp.actionFlowChartGraphic(processid, nodeid, nodeList, action, nodeType, startNodeid, endNodeid);
            }
            if (function.equals("getProcessList")) {
                reslutJson = imp.getProcessList(searchStr);
            }
            if (function.equals("deleteProcessList")) {
                reslutJson = imp.deleteProcessList(processid);
            }
            if (function.equals("getUnid")) {
                reslutJson = imp.getUnid();
            }

            // 保存流程表单字段配置
            if(function.equals("saveFormConfig")){
                reslutJson = imp.setFormConfig(formConfig,processid);
            }
            // 获取流程表单字段配置
            if(function.equals("getFormConfig")){
                reslutJson = imp.getFormConfig(processid);
            }

            // 获得所有节点按钮的动作信息
            if(function.equals("getALLActionConfig")){
                reslutJsonArr = imp.getALLActionConfig();
            }

            // 获取节点操作按钮
            if(function.equals("getButtonConfig")){
                reslutJson = imp.getButtonConfig(processid,nodeid);
            }

            // 保存配置节点操作按钮
            if(function.equals("setButtonConfig")){
                reslutJson = imp.setButtonConfig(nodeButtonConfig,processid,nodeid);
            }


        }else {
            ((HttpServletResponse) servletResponse).setStatus(404);
        }
        try {
            BeanCtx.getConnection().close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");

        if(!reslutJson.isEmpty()){
            servletResponse.getWriter().write(reslutJson.toString());
        }else {
            servletResponse.getWriter().write(reslutJsonArr.toString());
        }

        ((HttpServletResponse) servletResponse).setStatus(200);

    }

    private static void readProperty() {
        Properties properties = new Properties();
        InputStream inputStream = CommonFilter.class.getResourceAsStream("/config.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            driver = (String) properties.get("driver");
            url = (String) properties.get("url");
            username = (String) properties.get("username");
            password = (String) properties.get("password");
        } catch (NullPointerException e) {
            System.out.println("请配置数据库连接！");
        }

    }

    public void destroy() {

    }
}
