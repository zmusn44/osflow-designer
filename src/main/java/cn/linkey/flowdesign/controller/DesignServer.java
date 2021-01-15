package cn.linkey.flowdesign.controller;

import cn.linkey.flowdesign.api.FlowChart;
import cn.linkey.flowdesign.api.FlowchartImp;
import cn.linkey.flowdesign.utils.Tools;
import cn.linkey.orm.dao.Rdb;
import cn.linkey.orm.dao.impl.RdbImpl;
import cn.linkey.orm.factory.BeanCtx;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 流程定义接口服务
 */
@RestController
public class DesignServer {

    // 注入数据源对象
    @Resource
    private DruidDataSource dataSource;


    /**
     * 搜索流程
     *
     * @param searchStr 搜索字段，为空时返回所有流程
     * @return 返回搜索或所有流程信息
     */
    @RequestMapping(value = "/design/getProcessList", method = RequestMethod.GET)
    public JSONObject getProcessList(@RequestParam(name = "searchStr", required = false, defaultValue = "") String searchStr) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getProcessList(searchStr);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }

    /**
     * 新增流程id
     *
     * @return 返回新创建的流程ID
     */
    @RequestMapping(value = "/design/getUnid", method = RequestMethod.GET)
    public JSONObject getUnid() {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getUnid();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 获取流程图模型数据接口
     * 前端获得数据后，依据Processid和flowJSON的内容，重新将保存过的流程图模型进行渲染显示
     *
     * @param processid 流程id，非必须，为空时表示新建流程
     * @return {"status","0/1","msg":"提示信息","Processid":"36位UUID","flowJSON":"流程图模型数据"}
     */
    @RequestMapping(value = "/design/getFlowChartGraphic/{processid}", method = RequestMethod.GET)
    public JSONObject getFlowChartGraphic(@PathVariable("processid") String processid) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getFlowChartGraphic(processid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }

    /**
     * 获取人工活动节点配置的发送邮件信息 表BPM_MailConfig
     * 筛选条件：流程id AND 节点id
     *
     * @param unid 数据id，必须
     * @return {"status","0/1","msg":"提示信息","mailConfig":{row1}}
     * mailConfigRows 送邮件信息明细行，依据wf_lastmodified进行升序排序，
     * row1 邮件信息单行明细内容，字段包含 表BPM_MailConfig 中的所有字段（包含WF_OrUnid）
     */
    @RequestMapping(value = "/design/getFlowChartMailConfigByUnid/{unid}", method = RequestMethod.GET)
    public JSONObject getFlowChartMailConfigByUnid(@PathVariable("unid") String unid) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getFlowChartMailConfigByUnid(unid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 保存或更新人工活动节点配置的发送邮件信息到数据库表 BPM_MailConfig
     * 筛选条件：流程id AND 节点id
     * 注意：这里，如果是新建的数据，则insert一条，如果数据已经存在（WF_OrUnid判断）则进行update
     *
     * @param processid  流程id，必须
     * @param nodeid     节点id，必须（节点id，为前端流程图设计器生成）
     * @param formParmes 节点邮件配置表单F_S002_A025，中的所有字段信息组成JSON（包含 WF_OrUnid，新建时为空）
     * @return {"status","0/1","msg":"提示信息"}
     */
    @RequestMapping(value = "/design/saveFlowChartMailConfigByNodeType", method = RequestMethod.POST)
    public JSONObject saveFlowChartMailConfigByNodeType(@RequestParam(name = "Processid") String processid,
                                                        @RequestParam(name = "Nodeid") String nodeid,
                                                        @RequestParam(name = "json") String formParmes) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        if (formParmes == null) {
            formParmes = "";
        }
        JSONObject formJson = JSONObject.parseObject(formParmes);

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.saveFlowChartMailConfigByNodeType(processid, nodeid, formJson);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 删除人工活动节点配置的发送邮件信息 表BPM_MailConfig
     * 筛选条件：流程id AND 节点id
     *
     * @param docUnidList 需要删除行的 WF_OrUnid 键的值，多条记录使用逗号分隔
     * @return {"status","0/1","msg":"提示信息"}
     */
    @RequestMapping(value = "/design/deleteFlowChartMailConfigByNodeType", method = RequestMethod.POST)
    public JSONObject deleteFlowChartMailConfigByNodeType(@RequestParam(name = "docUnidList") String docUnidList) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.deleteFlowChartMailConfigByNodeType(docUnidList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 配置节点操作按钮
     *
     * @param buttonConfig 节点操作按钮配置
     * @param processid    对应流程ID
     * @param nodeid       对应用节点id
     * @return {"status","0/1","msg":"提示信息"}
     */
    @RequestMapping(value = "/design/setButtonConfig", method = RequestMethod.POST)
    public JSONObject setButtonConfig(@RequestParam(name = "Processid") String processid,
                                      @RequestParam(name = "Nodeid") String nodeid,
                                      @RequestParam(name = "nodeButtonConfig") String buttonConfig) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.setButtonConfig(buttonConfig, processid, nodeid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 删除流程设计
     *
     * @param processids 流程ID(用逗号","隔开)
     * @return 返回删除结果 "ok", "成功删除"+i+"条记录！"
     */
    @RequestMapping(value = "/design/deleteProcessList/{processid}", method = RequestMethod.POST)
    public JSONObject deleteProcessList(@PathVariable("processid") String processids) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.deleteProcessList(processids);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 获取流程图模型数据接口
     * 前端获得数据后，依据Processid和flowJSON的内容，重新将保存过的流程图模型进行渲染显示
     *
     * @param processid 流程id，非必须，为空时表示新建流程
     * @return {"status","0/1","msg":"提示信息","Processid":"36位UUID","flowJSON":"流程图模型数据"}
     */
    @RequestMapping(value = "/design/getFlowChartModByNodeType/{processid}/{nodeid}", method = RequestMethod.GET)
    public JSONObject getFlowChartModByNodeType(@PathVariable("processid") String processid, @PathVariable("nodeid") String nodeid) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getFlowChartModByNodeType(processid, nodeid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }

    /**
     * 获取各节点类型的事件统一请求接口，事件统一到表 BPM_EngineEventConfig 中依据筛选条件进行获取
     *
     * @param processid 流程id，必须
     * @param nodeid    节点id，必须（节点id，为前端流程图设计器生成）
     * @return {"status","0/1","msg":"提示信息","eventRows":[{row1},{row2}]}
     * eventRows 事件明细行（可能多行），依据sortnum和wf_lastmodified进行排序
     * row1 事件单行明细内容，字段包含有 WF_OrUnid、eventid、rulenum、params、sortnum、wf_lastmodified、wf_addname。
     */
    @RequestMapping(value = "/design/getFlowChartEventByNodeType/{processid}/{nodeid}")
    public JSONObject getFlowChartEventByNodeType(@PathVariable("processid") String processid, @PathVariable("nodeid") String nodeid) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getFlowChartEventByNodeType(processid, nodeid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }

    /**
     * 获取节点操作按钮配置
     *
     * @param processid 流程ID
     * @param nodeid    节点ID
     * @return 操作按钮配置JSON
     */
    @RequestMapping(value = "/design/getButtonConfig/{processid}/{nodeid}", method = RequestMethod.GET)
    public JSONObject getButtonConfig(@PathVariable("processid") String processid, @PathVariable("nodeid") String nodeid) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getButtonConfig(processid, nodeid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 获取人工活动节点配置的发送邮件信息 表BPM_MailConfig
     * 筛选条件：流程id AND 节点id
     *
     * @param processid 流程id，必须
     * @param nodeid    节点id，必须（节点id，为前端流程图设计器生成）
     * @return {"status","0/1","msg":"提示信息","mailConfigRows":[{row1},{row2}]}
     * mailConfigRows 送邮件信息明细行（可能多行），依据wf_lastmodified进行升序排序，
     * row1 邮件信息单行明细内容，字段包含 表BPM_MailConfig 中的所有字段（包含WF_OrUnid）
     */
    @RequestMapping(value = "/design/getFlowChartMailConfigByNodeType/{processid}/{nodeid}", method = RequestMethod.GET)
    public JSONObject getFlowChartMailConfigByNodeType(@PathVariable("processid") String processid, @PathVariable("nodeid") String nodeid) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getFlowChartMailConfigByNodeType(processid, nodeid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }

    /**
     * 表单字段配置
     *
     * @param processid 流程id，必须
     * @return 表单字段配置JSON
     */
    @RequestMapping(value = "/design/getFormConfig/{processid}", method = RequestMethod.GET)
    public JSONObject getFormConfig(@PathVariable("processid") String processid) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.getFormConfig(processid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }

    /**
     * 流程保存前操作，如缺省保存，删除节点，校验
     *
     * @param processid   流程ID
     * @param nodeid      节点ID
     * @param nodeList    节点ID的字符串，逗号隔开
     * @param action      CheckNodeAttr、SaveAllDefaultNode、DeleteNode
     * @param nodeType    节点类型
     * @param startNodeid 开始节点ID
     * @param endNodeid   结束节点ID
     * @return 操作结果
     */
    @RequestMapping(value = "/design/actionFlowChartGraphic", method = RequestMethod.POST)
    public JSONObject actionFlowChartGraphic(@RequestParam(name = "Processid", required = false) String processid,
                                             @RequestParam(name = "Nodeid", required = false) String nodeid,
                                             @RequestParam(name = "nodeList", required = false) String nodeList,
                                             @RequestParam(name = "Action", required = false) String action,
                                             @RequestParam(name = "NodeType", required = false) String nodeType,
                                             @RequestParam(name = "StartNodeid", required = false) String startNodeid,
                                             @RequestParam(name = "EndNodeid", required = false) String endNodeid) {


        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;


        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.actionFlowChartGraphic(processid, nodeid, nodeList, action, nodeType, startNodeid, endNodeid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }

    /**
     * 保存流程图模型数据接口
     * 保存流程图到表：BPM_ModGraphicList中，保存前必须验证在BPM_ModProcessList中有对应processid的流程图配置信息
     *
     * @param processid 流程id，必须
     * @param flowJSON  流程图模型数据，必须
     * @return {"status","0/1","msg":"提示信息"}
     */
    @RequestMapping(value = "/design/saveFlowChartGraphic", method = RequestMethod.POST)
    public JSONObject saveFlowChartGraphic(@RequestParam(name = "Processid", required = false) String processid,
                                           @RequestParam(name = "json", required = false) String flowJSON) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;


        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.saveFlowChartGraphic(processid, flowJSON);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 保存各节点类型的统一请求接口，这里依据节点id进行类另和保存表的区分
     * 节点类型包含：全局属性（Process，对应表 bpm_modprocesslist），
     * 人工节点（userTask/T10001，T开头，对应表 Bpm_Modtasklist），
     * 自由节点（businessRuleTask/T10003，T开头，对应表 Bpm_Modtasklist），
     * 路由节点（sequenceFlow/R10005，R开头，对应表 Bpm_ModSequenceflowList），
     * 网关节点（Gateway/G10007，G开头，对应表 Bpm_Modgatewaylist），
     * 事件节点（Event/E10009，E开头，对应表 Bpm_Modeventlist），
     * 开始节点（startEvent/E10011，E开头，对应表 Bpm_Modeventlist），
     * 结束节点（endEvent/E10013，E开头，对应表 Bpm_Modeventlist）.
     * 使用nodeid的开始字符，可以决定保存的表名，再使用流程id，两者做为条件，可以找到表中唯一的一条记录，
     * 若记录不存在则insert，若记录存在则update
     * 请注意：formParmes中有不少的字段，在对应的表中是不存在这些表字段的，
     * 所以实现存储时是将数据存在到表中的XmlData大字段的，这里建议使用ORM存储引擎中的Document对象进行存储
     *
     * @param processid   流程id，必须
     * @param nodeid      节点id，必须
     * @param extNodeType 节点类型，非必须，一般用于后端判断某些节点某些字段必填
     * @param formParmes  非必须，大多数据情况下是有值的，节点中表单需要保存的参数，仅对传入的参数进行保存，
     *                    这里为了后续扩展方便，不使用单独的字段，请遍历formParmes对象进行保存，需要保存什么，交给前端进行选择和决断
     * @return {"status","0/1","msg":"提示信息"}
     */
    @RequestMapping(value = "/design/saveFlowChartModByNodeType/{processid}/{nodeid}/{extnodetype}", method = RequestMethod.POST)
    public JSONObject saveFlowChartModByNodeType(@PathVariable("processid") String processid, @PathVariable("nodeid") String nodeid,
                                                 @PathVariable("extnodetype") String extNodeType, @RequestParam(name = "json", required = false) String formParmes) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        if (formParmes == null) {
            formParmes = "";
        }
        JSONObject formJson = JSONObject.parseObject(formParmes);

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.saveFlowChartModByNodeType(processid, nodeid, extNodeType, formJson);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 保存各节点类型的事件统一请求接口，事件统一保存到表 BPM_EngineEventConfig 中
     * 筛选条件：流程id AND 节点id
     * 处理逻辑：保存时，只处理表中符合筛选条件的所有行内容，以及前端传入的eventRows行内容。
     * 对于已存在的行（WF_OrUnid相同），做update，不存在的行，做insert，对比在表中，又不在eventRows中的，做delete处理。
     * 即每次保存，都以前端传入的eventRows做为保存的准确内容。
     *
     * @param processid 流程id，必须
     * @param nodeid    节点id，必须（节点id，为前端流程图设计器生成）
     * @param delRow    需要删除的记录
     * @param newRow    新增的记录，需要保存的事件明细列表。每行数据包含有 WF_OrUnid、eventid、rulenum、params、sortnum 等字段
     * @param editRow   修改的记录
     * @return {"status","0/1","msg":"提示信息"}
     */
    @RequestMapping(value = "/design/saveFlowChartEventByNodeType/{processid}/{nodeid}", method = RequestMethod.POST)
    public JSONObject saveFlowChartEventByNodeType(@PathVariable("processid") String processid, @PathVariable("nodeid") String nodeid,
                                                   @RequestParam(name = "delRow", required = false) String delRow,
                                                   @RequestParam(name = "newRow", required = false) String newRow,
                                                   @RequestParam(name = "editRow", required = false) String editRow) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        JSONArray delRowArry = JSONObject.parseArray(delRow);
        JSONArray newRowArry = JSONObject.parseArray(newRow);
        JSONArray editRowArry = JSONObject.parseArray(editRow);

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            if (newRowArry.size() > 0) {
                jsonObj.put("save", imp.saveFlowChartEventByNodeType(processid, nodeid, newRowArry));
            }
            if (editRowArry.size() > 0) {
                jsonObj.put("edit", imp.saveFlowChartEventByNodeType(processid, nodeid, editRowArry));
            }

            String docUnidList = "";
            for (int i = 0; i < delRowArry.size(); i++) {
                if (i > 0) {
                    docUnidList += "," + delRowArry.getJSONObject(i).get("WF_OrUnid");
                    continue;
                }
                docUnidList += delRowArry.getJSONObject(i).get("WF_OrUnid");
            }
            if (Tools.isNotBlank(docUnidList)) {
                jsonObj.put("delete", imp.deleteFlowChartEventByNodeType(processid, nodeid, docUnidList));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }


    /**
     * 设置表单字段配置
     *
     * @param formConfig 表单字段配置JSON字符串
     * @param processid  对应流程ID
     * @return {"status","0/1","msg":"提示信息"}
     */
    @RequestMapping(value = "/design/saveFormConfig/{processid}", method = RequestMethod.POST)
    public JSONObject saveFormConfig(@PathVariable("processid") String processid, @RequestParam(name = "formConfig", required = false) String formConfig) {

        FlowChart imp = new FlowchartImp();
        JSONObject jsonObj = new JSONObject();
        Connection conn = null;
        Rdb rdb;

        try {
            conn = dataSource.getConnection();

            // 初始化连接
            rdb = new RdbImpl(conn);
            BeanCtx.setConnection(conn);
            BeanCtx.setRdb(rdb);

            jsonObj = imp.setFormConfig(formConfig, processid);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.discardConnection(conn);
        }

        return jsonObj;

    }

}
