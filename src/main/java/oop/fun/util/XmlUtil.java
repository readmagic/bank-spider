package oop.fun.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.StringReader;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: bin.yu
 * Date: 2021/4/22
 * Time: 下午3:57
 */
public class XmlUtil {



    /**
     * 把xml文件转换为list形式，其中每个元素是一个map，map中的key为有值的节点名称，并以其所有的祖先节点为前缀，用
     * "."相连接。如：SubscribeServiceReq.Send_Address.Address_Info.DeviceType
     *
     * @param xmlStr xml内容
     * @return Map 转换为map返回
     */
    public static List<Map<String, String>> xml2List(String xmlStr, String idName) {
        List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
        Map<String, String> rtnMap = new HashMap<String, String>();
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            doc = builder.build(new StringReader(xmlStr));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ArrayList<Map<String, String>>();
        }
        // 得到根节点
        Element root = doc.getRootElement();
        String rootName = root.getName();
        // 调用递归函数，得到所有最底层元素的名称和值，加入map中
        convert2List(root, rtnMap, rootName, rtnList, idName);
        if (rtnList.size() == 0) {
            rtnList.add(rtnMap);
        }
        return rtnList;
    }

    public static void convert2List(Element e, Map<String, String> map,
                                    String lastname, List<Map<String, String>> list, String idName) {
        List children = e.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            String idValue = child.getAttributeValue("id");
            if (!idName.equals(idValue)) {
                continue;
            }


            List<Element> ch = child.getChildren();
            if (ch.size() > 0) {

                for (Element element : ch) {
                    List<Element> kColl = element.getChildren();
                    Map<String, String> aMap = new HashMap<String, String>();
                    aMap.put(kColl.get(0).getAttributeValue("id"), kColl.get(0).getAttributeValue("value"));
                    aMap.put(kColl.get(1).getAttributeValue("id"), kColl.get(1).getAttributeValue("value"));
                    list.add(aMap);
                }


            }

        }
    }

}