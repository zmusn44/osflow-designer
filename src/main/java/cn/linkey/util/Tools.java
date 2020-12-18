package cn.linkey.util;

import cn.linkey.orm.doc.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * 
 * @author Administrator
 * 
 */
public class Tools {
	/**
	 * 判断字符串是否为null或"null"或长度为0
	 * 
	 * @param string 要判断的字符串
	 * @return 判断结果，true为空，false为非空
	 */
	private static boolean isEmpty(String string) {
		return (string == null) || (string.equals("null")) || (string.length() == 0);
	}

	/**
	 * 判断字符串是否为空值或去掉前后空格长度为0
	 * 
	 * @param string 要判断的字符串
	 * @return 判断结果，true为空，false为非空
	 */
	public static boolean isBlank(String string) {
		return (isEmpty(string)) || (string.trim().length() == 0);
	}

	/**
	 * 判断字符串是否不为空值
	 * 
	 * @param string 要判断的字符串
	 * @return 判断结果，true为非空，false为空
	 */
	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}

	/**
	 * 判断字符串是否由字母数字和下划线组成
	 * 
	 * @param str 要判断的字符串
	 * @return 返回true表示成立，false表示不成立
	 */
	public static Boolean isString(String str) {
		if (Tools.isBlank(str)) {
			return true;
		}
		Boolean bl = false;
		// 首先,使用Pattern解释要使用的正则表达式，其中^表是字符串的开始，$表示字符串的结尾。
		Pattern pt = Pattern.compile("^[0-9a-zA-Z_.]+$");
		Matcher mt = pt.matcher(str);
		if (mt.matches()) {
			bl = true;
		}
		return bl;

	}
	
	/**
     * 根据节点id获得节点所在数据库表名
     * 
     * @param nodeid
     * @return 数据库表名
     */
    public static String getNodeTableName(String processid, String nodeid) {
        String nodeType = getNodeType(processid, nodeid);
        if (Tools.isBlank(nodeType)) {
            return "";
        }
        return "BPM_Mod" + nodeType + "List";
    }
    
    /**
     * 根据节点id获得节点的基本类型名称
     * 
     * @param nodeid
     * @return 数据库表名
     */
    public static String getNodeType(String processid, String nodeid) {
        // 根据节点首字母得到，速度最快
        if (Tools.isBlank(nodeid)) {
            return "";
        }
        String nodeType = nodeid.substring(0, 1);
        if (nodeType.equals("T")) {
            nodeType = "Task"; // 任务
        }
        else if (nodeType.equals("R")) {
            nodeType = "SequenceFlow"; // 路由
        }
        else if (nodeType.equals("G")) {
            nodeType = "Gateway"; // 网关
        }
        else if (nodeType.equals("P")) {
            nodeType = "Process"; // 流程
        }
        else if (nodeType.equals("E")) {
            nodeType = "Event"; // 事件
        }
        else if (nodeType.equals("S")) {
            nodeType = "SubProcess"; // 子流程
        }
        else {
            return "";
        }
        return nodeType;

        // 从视图中去得到，速度慢一点
        /*
         * String sql="select NodeType from BPM_AllModNodeList where Processid='"+processid+"' and Nodeid='"+nodeid+"'"; String nodeType=Rdb.getValueTopOneBySql(sql); return nodeType;
         */
    }



	/**
	 * 文档数组对像转为json字符串
	 *
	 * @param dc 文档集合使用Rdb.GetAllDocumentsBySql()获得
	 * @param key 是否需要使用key关键字进行输出如传入rows输出结果为 {"rows":[{json1},{json2}]} 否则传入空值标准输出为 [{json1},{json2}]
	 * @param useTableConfig true 表示使用配置表中的字段信息进行输出,false 表示使用数据库表中的字段信息进行输出
	 * @return 返回json字符串，格式由是否有key决定
	 */
	public static String dc2json(Document dc[], String key, boolean useTableConfig) {
		StringBuilder jsonStr = new StringBuilder();
		if (cn.linkey.orm.util.Tools.isBlank(key)) {
			jsonStr.append("[");
		}
		else {
			jsonStr.append("{\"" + key + "\":[");
		}
		int i = 0;
		for (Document doc : dc) {
			if (i == 0) {
				i = 1;
			}
			else {
				jsonStr.append(",");
			}
			jsonStr.append(doc.toJson(useTableConfig));
		}
		if (cn.linkey.orm.util.Tools.isBlank(key)) {
			jsonStr.append("]");
		}
		else {
			jsonStr.append("]}");
		}
		return jsonStr.toString();
	}
}
