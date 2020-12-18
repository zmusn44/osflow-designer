package cn.linkey.flowchart.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.orm.dao.Rdb;
import cn.linkey.orm.doc.Document;
import cn.linkey.orm.factory.BeanCtx;
import cn.linkey.util.Tools;

public class FlowchartImp implements FlowChart{
	

	
	
	@Override
	public JSONObject saveFlowChartGraphic(String processid, String flowJSON) {
		Rdb rdb = BeanCtx.getRdbBean();
		JSONObject resultJson = new JSONObject();
        String sql = "select Processid from BPM_ModProcessList where Processid='" + processid + "'";
        if (rdb.hasRecord(sql)) {
            sql = "select * from BPM_ModGraphicList where Processid='" + processid + "'";
            Document doc = rdb.getDocumentBySql(sql);
            doc.s("GraphicBody", flowJSON);
            doc.s("Processid", processid);
            doc.s("FlowType", "2");
            doc.save();
            resultJson.put("Status","1");
        }
        else {
        	resultJson.put("Status","0");
        	resultJson.put("msg", "请在空白处点击键并在过程属性中指定流程的名称!");
        }
        return resultJson;
	}

	@Override
	public JSONObject getFlowChartGraphic(String processid) {
		String processName = "", sql = "";
		Rdb rdb = BeanCtx.getRdbBean();
		JSONObject resultJson = new JSONObject();
		if (Tools.isBlank(processid)) {
			processid = rdb.getNewUnid();
			processName = "新建流程";
		} else {
			sql = "select * from BPM_ModProcessList where Processid='" + processid + "'";
			Document doc = rdb.getDocumentBySql(sql);
			if (doc.isNull()) {
				resultJson.put("0", "流程(" + processid + ")不存在!");
				return resultJson;
			} else {
				processName = doc.g("NodeName");
			}
		}
		String sql1 = "select GraphicBody from bpm_modgraphiclist where Processid='" + processid + "' and FlowType='2'";
		String flowJSON = rdb.getValueBySql(sql1);

//		BeanCtx.p(RestUtil.formartResultJson("1", processName, flowJSON));
		resultJson = JSONObject.parseObject("{\"Status\":\"1\",\"msg\":\"" + processName + "\",\"flowJSON\": " + (Tools.isBlank(flowJSON)?"\"\"":flowJSON) + ",\"Processid\": \"" + processid + "\" }");
		//		        }
		return resultJson;
	}

	@Override
	public JSONObject saveFlowChartModByNodeType(String processid, String nodeid, String extNodeType,
			JSONObject formParmes) {
		JSONObject json = new JSONObject();
		if (extNodeType.equals("Process")) {
			json = saveProcess(processid, nodeid, extNodeType, formParmes);
        }
        else {
        	json = saveNode(processid, nodeid, extNodeType,formParmes);
        }
        
		return json;
	}

	@Override
	public JSONObject getFlowChartModByNodeType(String processid, String nodeid) {
		Rdb rdb = BeanCtx.getRdbBean();
		JSONObject resultJson = new JSONObject();
		String nodeTableName = Tools.getNodeTableName(processid, nodeid); //节点所在数据库表
	    String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
		Document doc = rdb.getDocumentBySql(sql);
		resultJson.put("基本属性", JSONObject.parse(doc.toJson()));
		
		sql = "select * from  BPM_EngineEventConfig where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
		Document [] docs = rdb.getAllDocumentsBySql(sql);
		JSONArray list = new JSONArray();
		for(Document d : docs) {
			list.add(d.toJson());
		}
		resultJson.put("事件设置", list);
		
		sql = "select * from  BPM_ModProcessList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
		doc = rdb.getDocumentBySql(sql);
		resultJson.put("归档设置", JSONObject.parse(doc.toJson()));
		
		if(nodeid.substring(0, 1).equals("T")) {
			nodeTableName = "BPM_MailConfig";
			sql = "select * from  "+ nodeTableName +" where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
			docs = rdb.getAllDocumentsBySql(sql);
			list.clear();
			for(Document d : docs) {
				list.add(d.toJson());
			}
			resultJson.put("邮件设置", list);
		}
		
		sql = "select * from  BPM_ModTaskList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
		doc = rdb.getDocumentBySql(sql);
		resultJson.put("移动设备", JSONObject.parse(doc.toJson()));
		
		return resultJson;
	}

	@Override
	public JSONObject saveFlowChartEventByNodeType(String processid, String nodeid, JSONArray eventRows) {
		Rdb rdb = BeanCtx.getRdbBean();
		JSONObject json = JSONObject.parseObject(eventRows.get(0).toString());
		int index = 0;
		if(eventRows.size()>0){
			for(int i=0;i<eventRows.size();i++){
				JSONObject job = eventRows.getJSONObject(i); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
				String sql = "select * from BPM_EngineEventConfig where WF_OrUnid = '" + job.get("WF_OrUnid") + "'";
				Document doc = rdb.getDocumentBySql(sql);
				if(Tools.isBlank(doc.g("WF_OrUnid"))) {
					doc.s("WF_OrUnid",rdb.getNewUnid());
				}
				doc.s("Processid",processid);
				doc.s("Nodeid",nodeid);
				doc.s("Eventid",job.get("Eventid"));
				doc.s("RuleNum",job.get("RuleNum"));
				doc.s("Params",job.get("Params"));
				doc.s("SortNum",job.get("SortNum"));
				doc.s("WF_LastModified",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				doc.save();
				index++;
			}
		}
		JSONObject result = new JSONObject();
		if(index == eventRows.size()) {
			result.put("1", "事件保存成功！");
		}else {
			result.put("msg", "事件保存失败！");
		}
		return result;
	}

	@Override
	public JSONObject getFlowChartEventByNodeType(String processid, String nodeid) {
		Rdb rdb = BeanCtx.getRdbBean();
		String sql = "select * from BPM_EngineEventConfig where Processid = '"+processid+"' and Nodeid = '"+ nodeid +"'";
		Document [] docs = rdb.getAllDocumentsBySql(sql);
		JSONArray jsonarr = new JSONArray();
		for(Document doc : docs) {
			jsonarr.add(JSONObject.parse(doc.toJson()));
		}
		JSONObject json = new JSONObject();
		json.put("rows", jsonarr);
		return json;
	}

	@Override
	public JSONObject saveFlowChartMailConfigByNodeType(String processid, String nodeid, JSONObject formParmes) {
		Rdb rdb = BeanCtx.getRdbBean();
		String sql = "select * from BPM_MailConfig where WF_OrUnid = '"+formParmes.get("WF_OrUnid")+"'";
		Document doc = rdb.getDocumentBySql(sql);
		if(Tools.isBlank(doc.g("WF_OrUnid"))) {
			doc.s("WF_OrUnid",rdb.getNewUnid());
			doc.s("Processid",processid);
			doc.s("Nodeid",nodeid);
		}
		doc.s("SendTo",formParmes.get("SendTo"));
		doc.s("CopyTo",formParmes.get("CopyTo"));
		doc.s("MailTitle",formParmes.get("MailTitle"));
		doc.s("MailBody",formParmes.get("MailBody"));
		doc.s("Actionid",formParmes.get("Actionid"));
		doc.s("WF_LastModified",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		int i = doc.save();
		JSONObject json = new JSONObject();
        if (i > 0) {
        	json.put("ok", "保存成功");
        }
        else {
        	json.put("error", "保存失败");
        }
		return json;
	}

	@Override
	public JSONObject deleteFlowChartMailConfigByNodeType(String processid, String nodeid, String docUnidList) {
		String [] unids = docUnidList.split(",");
		Rdb rdb = BeanCtx.getRdbBean();
		int i = 0;
		for(String unid : unids) {
			String sql = "delete from BPM_MailConfig where WF_OrUnid = '"+unid+"'";
			if(rdb.execSql(sql)>0) {
				i++;
			}
		}
		JSONObject json = new JSONObject();
    	json.put("ok", "成功删除"+i+"条记录！");
		return json;
	}

	@Override
	public JSONObject getFlowChartMailConfigByNodeType(String processid, String nodeid) {
		Rdb rdb = BeanCtx.getRdbBean();
		String sql = "select * from BPM_MailConfig where Processid = '"+processid+"' and Nodeid = '"+ nodeid +"'";
		Document [] docs = rdb.getAllDocumentsBySql(sql);
		JSONArray jsonarr = new JSONArray();
		for(Document doc : docs) {
			jsonarr.add(JSONObject.parse(doc.toJson()));
		}
		JSONObject json = new JSONObject();
		json.put("rows", jsonarr);
		return json;
	}
	
	@Override
	public JSONObject getFlowChartMailConfigByUnid(String unid) {
		Rdb rdb = BeanCtx.getRdbBean();
		String sql = "select * from BPM_MailConfig where WF_OrUnid = '"+unid+"'";
		Document doc = rdb.getDocumentBySql(sql);
		JSONObject json = new JSONObject();
		json = (JSONObject) JSONObject.parse(doc.toJson());
		return json;
	}
	
    /**
     * 存盘流程过程属性
     */
    public JSONObject saveProcess(String processid,String nodeid,String extNodeType,JSONObject formParmes) {
        String nodeTableName = Tools.getNodeTableName(processid, nodeid); //节点所在数据库表
    	String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Rdb rdb = BeanCtx.getRdbBean();
        Document nodeDoc = rdb.getDocumentBySql(sql);
        
//        nodeDoc.appendFromRequest(BeanCtx.getRequest());
        //录入数据
        for(String key : formParmes.keySet()) {
        	nodeDoc.s(key, formParmes.get(key));
        }
        nodeDoc.s("Processid", processid);
        nodeDoc.s("Nodeid", nodeid);
        nodeDoc.s("ExtNodeType", extNodeType);
        nodeDoc.s("WF_Appid", "S029");
        int i = nodeDoc.save();
        JSONObject json = new JSONObject();
        if (i > 0) {
        	json.put("ok", "保存成功");
        }
        else {
        	json.put("error", "保存失败");
        }
        return json;
    }
    
    @Override
    public JSONObject actionFlowChartGraphic(String processid, String nodeid, String nodeList, String action,String nodeType,String startNodeid,String endNodeid) {
    	if (action.equals("CheckNodeAttr")) {
    		return checkAllNodeAttr(processid,nodeList);
        }
        else if (action.equals("SaveAllDefaultNode")) {
        	return saveAllDefaultNodeAttr(processid,nodeid,nodeType,startNodeid,endNodeid);
        }
        else if (action.equals("DeleteNode")) {
        	return deleteNode(processid,nodeid);
        }
    	return null;
    }
    
    /**
     * 存盘通用节点
     */
    public JSONObject saveNode(String processid,String nodeid,String extNodeType,JSONObject formParmes) {
         String nodeTableName = Tools.getNodeTableName(processid, nodeid); //节点所在数据库表
         String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
         Rdb rdb = BeanCtx.getRdbBean();
         Document nodeDoc = rdb.getDocumentBySql(sql);
         if(Tools.isBlank(nodeDoc.g("WF_OrUnid"))) {nodeDoc.s("WF_OrUnid", rdb.getNewUnid());}
         nodeDoc.s("ExtNodeType", extNodeType);
         nodeDoc.s("Processid", processid);
         nodeDoc.s("Nodeid", nodeid);
         for(String key : formParmes.keySet()) {
         	nodeDoc.s(key, formParmes.get(key));
         }
         nodeDoc.removeItem("QryNodeType");
         int i = nodeDoc.save();
         JSONObject json = new JSONObject();
         if (i > 0) {
         	json.put("ok", "保存成功");
         }
         else {
         	json.put("error", "保存失败");
         }
//         BeanCtx.userlog(processid, "修改流程节点", "修改流程节点(" + nodeDoc.g("NodeName") + ")");
    	return json;
    }
    
    public JSONObject deleteFlowChartEventByNodeType(String processid, String nodeid, String docUnidList ) {
		String [] unids = docUnidList.split(",");
		Rdb rdb = BeanCtx.getRdbBean();
		int i = 0;
		for(String unid : unids) {
			String sql = "delete from BPM_EngineEventConfig where WF_OrUnid = '"+unid+"'";
			if(rdb.execSql(sql)>0) {
				i++;
			}
		}
		JSONObject json = new JSONObject();
    	json.put("ok", "成功删除"+i+"条记录！");
		return json;
	}
    
    public JSONObject getProcessList(String searchStr) {
    	String sql = "select * from bpm_modprocesslist";
    	if(Tools.isNotBlank(searchStr)) {
    		sql += " where NodeName like '%"+searchStr+"%'";
    	}

    	sql += " Order by WF_DocCreated DESC";

        Rdb rdb = BeanCtx.getRdbBean();
        Document [] docs = rdb.getAllDocumentsBySql(sql);
        JSONArray jsonarr = new JSONArray();
		for(Document doc : docs) {
			jsonarr.add(JSONObject.parse(doc.toJson()));
		}
		JSONObject json = new JSONObject();
		json.put("rows", jsonarr);
        return json;
    	
    }
    
    @Override
    public JSONObject deleteProcessList(String processids) {
		String [] processidArr = processids.split(",");
		Rdb rdb = BeanCtx.getRdbBean();
		int i = 0;
		for(String processid : processidArr) {
			String sql = "delete from bpm_modprocesslist where WF_OrUnid = '"+processid+"'";
			if(rdb.execSql(sql)>0) {
				i++;
			}
		}
		JSONObject json = new JSONObject();
    	json.put("ok", "成功删除"+i+"条记录！");
		return json;
	}

	/**
	 * 表单字段配置
	 * add by alibao 202010
	 * @return 表单字段配置JSON
	 */
	@Override
	public JSONObject getFormConfig(String processid) {

		String sql = "select formConfig from bpm_modprocesslist where Processid = '" + processid + "'";
		Rdb rdb = BeanCtx.getRdbBean();
		String formConfig = rdb.getValueBySql(sql);

		return JSONObject.parseObject(formConfig);
	}

	/**
	 * 设置表单字段配置
	 * add by alibao 202010
	 * @param formConfig 表单字段配置JSON字符串
	 * @param processid  对应流程ID
	 * @return {"status","0/1","msg":"提示信息"}
	 */
	@Override
	public JSONObject setFormConfig(String formConfig, String processid) {

		String sql = "select * from bpm_modprocesslist where Processid = '" + processid + "'";

		Rdb rdb = BeanCtx.getRdbBean();
		Document doc = rdb.getDocumentBySql(sql);

		if(Tools.isBlank(doc.g("WF_OrUnid"))){
			doc.s("WF_OrUnid",rdb.getNewUnid());
		}

		doc.s("formConfig",formConfig);

		doc.save();

		JSONObject returnJSON = new JSONObject();
		returnJSON.put("status","1");
		returnJSON.put("msg","操作成功~");

		return returnJSON;
	}

	/**
	 * 通用删除表单记录方法
	 * add by alibao 202010
	 * @param tableName 需要删除的表名称
	 * @param wforunid  需要删除的唯一字段WF_Orunid
	 * @return {"status","0/1","msg":"提示信息"}
	 */
	@Override
	public JSONObject delCommonTableRows(String tableName, String wforunid) {

		String sql = "delete from " + tableName + " where WF_OrUnid='" + wforunid + "'";

		Rdb rdb = BeanCtx.getRdbBean();
		int i = rdb.execSql(sql);

		JSONObject returnJSON = new JSONObject();
		if(i>0){
			returnJSON.put("status","1");
			returnJSON.put("msg","删除成功~");
		}else{
			returnJSON.put("status","0");
			returnJSON.put("msg","删除出错！");
		}
		return returnJSON;
	}

	/**
	 * 获取节点操作按钮配置
	 * add by alibao 202010
	 * @param processid  流程ID
	 * @param nodeid     节点ID
	 * @return 操作按钮配置JSON
	 */
	@Override
	public JSONObject getButtonConfig(String processid, String nodeid) {

		String sql = "select nodeButtonConfig from bpm_modtasklist where Processid = '" + processid + "' and Nodeid='" + nodeid + "'";
		Rdb rdb = BeanCtx.getRdbBean();
		String nodeButtonConfig = rdb.getValueBySql(sql);

		if(Tools.isBlank(nodeButtonConfig)){
			nodeButtonConfig="{}";}

		return JSONObject.parseObject(nodeButtonConfig);

	}

	/**
	 * 配置节点操作按钮
	 * add by alibao 202010
	 * @param buttonConfig 节点操作按钮配置
	 * @param processid  对应流程ID
	 * @param nodeid      对应用节点id
	 * @return {"status","0/1","msg":"提示信息"}
	 */
	@Override
	public JSONObject setButtonConfig(String buttonConfig, String processid, String nodeid) {

		String sql = "select * from bpm_modtasklist where Processid='" + processid + "' and Nodeid='" + nodeid + "'";

		Rdb rdb = BeanCtx.getRdbBean();
		Document doc = rdb.getDocumentBySql(sql);

		if(Tools.isBlank(doc.g("WF_OrUnid"))){
			doc.s("WF_OrUnid",rdb.getNewUnid());
		}

		doc.s("nodeButtonConfig",buttonConfig);

		doc.save();

		JSONObject returnJSON = new JSONObject();
		returnJSON.put("status","1");
		returnJSON.put("msg","操作成功~");

		return returnJSON;

	}


	/**
	 * 获得所有节点按钮的动作信息
	 * add by alibao 202010
	 * [{"ActionName":"提交下一会签用户","Actionid":"GoToNextParallelUser"},{"ActionName":"收回文档","Actionid":"Undo"},{"ActionName":"结束当前环节并推进到下一环节","Actionid":"GoToNextNode"},{"ActionName":"回退首环节","Actionid":"GoToFirstNode"},{"ActionName":"尝试结束子流程节点","Actionid":"EndSubProcessNode"},{"ActionName":"回退上一环节","Actionid":"GoToPrevNode"},{"ActionName":"暂停","Actionid":"Pause"},{"ActionName":"标记为阅","Actionid":"EndCopyTo"},{"ActionName":"后台启动用户任务","Actionid":"StartUser"},{"ActionName":"办理完成","Actionid":"EndUserTask"},{"ActionName":"转他人处理","Actionid":"GoToOthers"},{"ActionName":"归档","Actionid":"GoToArchived"},{"ActionName":"后台结束用户任务","Actionid":"EndUser"},{"ActionName":"提交任意环节","Actionid":"GoToAnyNode"},{"ActionName":"回退上一用户","Actionid":"GoToPrevUser"},{"ActionName":"后台结束节点","Actionid":"EndNode"},{"ActionName":"回退任意环节","Actionid":"ReturnToAnyNode"},{"ActionName":"返回给转交者","Actionid":"BackToDeliver"},{"ActionName":"恢复","Actionid":"UnPause"},{"ActionName":"提交下一串行用户","Actionid":"GoToNextSerialUser"},{"ActionName":"自动运行","Actionid":"AutoRun"},{"ActionName":"返回给回退者","Actionid":"BackToReturnUser"},{"ActionName":"传阅用户","Actionid":"CopyTo"},{"ActionName":"同步任务","Actionid":"SyncUserTask"}]
	 * @return 返回所有按钮动作 JSON信息
	 */
	@Override
	public JSONArray getALLActionConfig() {

		String sql = "select ActionName,Actionid from bpm_engineactionconfig";

		Rdb rdb = BeanCtx.getRdbBean();
		Document[] docs = rdb.getAllDocumentsBySql(sql);
		String atcionJsonStr = Tools.dc2json(docs,"",false);

		return JSONArray.parseArray(atcionJsonStr);
	}



	@Override
    public JSONObject getUnid() {
    	Rdb rdb = BeanCtx.getRdbBean();
    	JSONObject json = new JSONObject();
    	json.put("Processid", rdb.getNewUnid());
		return json;
	}
    /**
     * 删除指定节点
     * 
     * @param processid
     */
    public JSONObject deleteNode(String processid,String nodeid) {
    	JSONObject result = new JSONObject();
//        String nodeid = BeanCtx.g("Nodeid", true);
    	Rdb rdb = BeanCtx.getRdbBean();
        String tableName = Tools.getNodeTableName(processid, nodeid);
        if (Tools.isNotBlank(tableName)) {
            String sql = "delete from " + tableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
            rdb.execSql(sql);
        }
        result.put("ok", "节点成功删除!");
        return result;
    }

    /**
     * 检测所有节点是否有保存属性
     */
    public JSONObject checkAllNodeAttr(String processid,String nodeList) {
    	JSONObject result = new JSONObject();
    	Rdb rdb = BeanCtx.getRdbBean();
        HashSet<String> noAttrNode = new HashSet<String>();
//        String nodeList = BeanCtx.g("NodeList", true);
        String[] nodeArray = cn.linkey.orm.util.Tools.split(nodeList, ",");
        for (String itemid : nodeArray) {
            int spos = itemid.indexOf("#");
            String nodeid = itemid.substring(0, spos);
            String objid = itemid.substring(spos + 1);
            String sql = "select * from BPM_AllModNodeList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
            if (!rdb.hasRecord(sql)) {
                noAttrNode.add(objid);
            }
        }
        result.put("ok", cn.linkey.orm.util.Tools.join(noAttrNode, ","));
        return result;
    }

    /**
     * 保存节点的所有缺省属性
     */
    public JSONObject saveAllDefaultNodeAttr(String processid,String nodeid, String nodeType, String startNodeid,String endNodeid ) {
    	JSONObject result = new JSONObject();
    	Rdb rdb = BeanCtx.getRdbBean();
        String sql = "select * from BPM_AllModNodeList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document doc = rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            //说明节点没有保存过，进行保存
            doc.s("Processid", processid);
            doc.s("Nodeid", nodeid);
            doc.s("NodeType", Tools.getNodeType(processid, nodeid));
            doc.setTableName(Tools.getNodeTableName(processid, nodeid));
            if (nodeType.equals("Router")) {
                //保存路由线
                // BeanCtx.out("startnodeid="+BeanCtx.g("StartNodeid"));
                doc.s("ExtNodeType", "sequenceFlow");
                doc.s("SourceNode", startNodeid);
                doc.s("TargetNode", endNodeid);
                doc.s("NodeName", "");
                doc.setTableName("BPM_ModSequenceFlowList");
            }
            else if (nodeType.equals("EndNode")) {
                //结束节点
                doc.s("NodeName", "结束");
                doc.s("ExtNodeType", "endEvent");
                doc.s("Terminate", "1");
                doc.s("EndBusinessName", "已结束");
                doc.s("EndBusinessid", "1");
                doc.setTableName("BPM_ModEventList");
            }
            else if (nodeType.equals("StartNode")) {
                //开始节点
                doc.s("NodeName", "开始");
                doc.s("ExtNodeType", "startEvent");
                doc.setTableName("BPM_ModEventList");
            }
            else if (nodeType.equals("Event")) {
                //事件节点
                doc.s("NodeName", "");
                doc.setTableName("BPM_ModEventList");
            }
            //BeanCtx.setDebug();
            int i = doc.save();
        }
        result.put("ok", "ok");
        return result;
    }
    
}
