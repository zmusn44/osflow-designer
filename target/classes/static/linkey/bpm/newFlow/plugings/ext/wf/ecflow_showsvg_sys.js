﻿var EndNodeColor1="#f8feb0";var EndNodeColor2="#fad01e";var EndNodeColor1="#3f6";var EndNodeColor2="#38e100";var CurrentNodeColor1="#ffa6a6";var CurrentNodeColor2="#ff0000";var OrNodeColor="#92cddc";var NoStartNodeColor1="#e7e7e7";var NoStartNodeColor2="#d7d7d7";var goalClickStyle="";var goalPrvNode=new Array();var goalNextNode=new Array();if((typeof Range!=="undefined")&&!Range.prototype.createContextualFragment){Range.prototype.createContextualFragment=function(a){var b=document.createDocumentFragment(),div=document.createElement("div");b.appendChild(div);div.outerHTML=a;return b}}var GoalStrokeWidth="";function ClickChangeObjStyle(e,a){var b=e.srcElement?e.srcElement:e.target;var c=b.id;if(c==""){return}var d=document.getElementById(c);if(d&&c!="svg"){if(a=="click"){GoalStrokeWidth=d.getAttribute("stroke-width");d.setAttribute("stroke-width","3")}else{if(GoalStrokeWidth=="3"){GoalStrokeWidth="1.5"}d.setAttribute("stroke-width",GoalStrokeWidth)}}}function SetProperty(a,b){}function InitNode(){var a=document.getElementsByTagName("*");for(var i=0;i<a.length;i++){var b=a[i];if(EndNodeList!=""){if((","+EndNodeList+",").indexOf(","+b.id+",")!=-1){b.setAttribute("fill","url(#EndActivity)")}}if(CurrentNodeid!=""){if((","+CurrentNodeid+",").indexOf(","+b.id+",")!=-1){b.setAttribute("fill","url(#CurrentActivity)")}}}}function CancelPrvNextNode(){var e="";var f="";var g="";var h="";for(i=0;i<goalPrvNode.length;i++){cancelNodeError(goalPrvNode[i])}for(i=0;i<goalNextNode.length;i++){cancelNodeError(goalNextNode[i])}function cancelNodeError(a){f=a.split(",");for(j=0;j<f.length;j++){g=f[j].split("$$");var b=g[0];var c=eval(b);var d=g[1];if(c.tagName=="polyline"){c.strokecolor=d}else{if(j==0){c.fillcolor=d}else{c.firstChild.color2=d}}}}goalPrvNode=new Array();goalNextNode=new Array()}var PrvNodeObj;var CurNodeid="";var goalsNum=0;var EndNodeArray=new Array();function play(){goalsNum=0;goalendNum=1;setTimeout("PlayNode()",1000)}function PlayTrace(){goalsNum=0;if(EndNodeList==""){return false}var a=document.getElementsByTagName("*");for(i=0;i<a;i++){var b=a[i];if(b.getAttribute("NodeType")=="StartNode"||b.getAttribute("NodeType")=="EndNode"){EndNodeList=b.Nodeid+","+EndNodeList}}EndNodeArray=EndNodeList.split(",");CancelCurNodeid();PlayNode()}function CancelCurNodeid(){if(CurrentNodeid=="")return false;var a=document.getElementsByTagName("*");for(var i=0;i<a.length;i++){var b=a[i];if((","+CurrentNodeid+",").indexOf(","+b.id+",")!=-1){b.setAttribute("fill","url(#EndActivity)")}}}function HightLightCurNodeid(){if(CurrentNodeid=="")return false;var a=document.getElementsByTagName("*");for(var i=0;i<a.length;i++){var b=a[i];if((","+CurrentNodeid+",").indexOf(","+b.id+",")!=-1){b.setAttribute("fill","url(#CurrentActivity)")}}}function PlayNode(){if(PrvNodeObj){PrvNodeObj.setAttribute("fill","url(#EndActivity)")}if(goalsNum<EndNodeArray.length){CurNodeid=EndNodeArray[goalsNum]}else{HightLightCurNodeid();return false}try{var a=document.getElementsByTagName("*");for(var i=0;i<a.length;i++){var b=a[i];if(b.id==CurNodeid){b.setAttribute("fill","url(#CurrentActivity)");PrvNodeObj=b}}}catch(e){alert(e.message)}goalsNum++;setTimeout("PlayNode()",1000)}Ext.onReady(function(){var r=GetUrlArg("Processid");var s="";var t=GetUrlArg("DocUnid");Ext.get("svg").on('contextmenu',function(e){var j=e.srcElement?e.srcElement:e.target;if(j.tagName=="rect"||j.tagName=="path"){var k=new Ext.menu.Menu();var l=j.id;var m=j.getAttribute("fill");if(l==undefined){return}if(j.tagName=="rect"){var n="page?wf_num=P_S003_001&DocUnid="+t+"&Nodeid="+l;if(m=="url(#EndActivity)"||m=="url(#CurrentActivity)"){k.add(new Ext.menu.Item({text:wflang.show_msg01,url:n,handler:ShowNodeRemark}))}else{k.add(new Ext.menu.Item({text:wflang.show_msg01,disabled:true}))}}else{k.add(new Ext.menu.Item({text:wflang.show_msg01,disabled:true}))}if(m=="url(#CurrentActivity)"){if(j.tagName!="path"){var o=new Ext.menu.Item({text:wflang.show_msg02,menu:{items:[]}});Ext.Ajax.request({url:'rule?wf_num=R_S003_B047',method:'POST',success:function(a,b){var c=Ext.util.JSON.decode(a.responseText);var d=c.item.split(",");for(i=0;i<d.length;i++){var e=d[i];var f=new Ext.menu.Item({text:e,icon:'linkey/bpm/images/icons/user_green.gif'});o.menu.add(f)}},params:{Processid:r,DocUnid:t,Nodeid:l,Action:'Current'}});k.add(o)}}if((m=="url(#EndActivity)"||m=="url(#CurrentActivity)")){if(j.tagName!="path"){var p=new Ext.menu.Item({text:wflang.show_msg03,menu:{items:[]}});Ext.Ajax.request({url:'rule?wf_num=R_S003_B047',method:'POST',success:function(a,b){var c=Ext.util.JSON.decode(a.responseText);var d=c.item.split(",");for(i=0;i<d.length;i++){var e=d[i];if(e=="")e=wflang.show_msg04;var f=new Ext.menu.Item({text:e,icon:'linkey/bpm/images/icons/user_green.gif'});p.menu.add(f)}},params:{Processid:r,DocUnid:t,Nodeid:l,Action:'End'}});k.add(p)}}if(j.tagName=="path"){if(j.fillcolor!=OrNodeColor){var q=new Ext.menu.Item({text:wflang.show_msg05,menu:{items:[]}});var n='rule?wf_num=R_S003_B063';Ext.Ajax.request({url:n,method:'GET',success:function(a,b){var c=Ext.util.JSON.decode(a.responseText);var d=c.item.split(",");for(i=0;i<d.length;i++){var e=d[i].split("$");var f=e[0];var g=e[1];var h=new Ext.menu.Item({text:f,icon:'linkey/bpm/images/icons/doclist.gif',url:g,handler:ShowSubDoc});if(f==wflang.show_msg06){h.setDisabled(true)}q.menu.add(h)}},params:{Processid:r,DocUnid:t,Nodeid:l,wf_appid:top.GetUrlArg("WF_Appid")}})}else{var q=new Ext.menu.Item({text:wflang.show_msg05,disabled:true})}k.add(q)}e.preventDefault();k.showAt(e.getXY())}});InitNode()});function ShowNodeRemark(a){OpenUrl(a.url,300,300)}function ShowSubDoc(a){OpenUrl(a.url,100,100)}