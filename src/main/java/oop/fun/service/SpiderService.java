package oop.fun.service;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import oop.fun.entity.SubBank;
import oop.fun.util.HttpUtil;
import oop.fun.util.XmlUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: frandy
 * Date: 2021/4/22
 * Time: 下午3:40
 */
public class SpiderService {
    private static Map<String, String> provinces = Maps.newHashMap();
    private static String provinceApi = "https://www.hebbank.com/corporbank/cityQueryAjax.do";
    private static String bankApi = "https://www.hebbank.com/corporbank/bankQueryAjax.do";
    private static Map<String, String> cityAlias = Maps.newHashMap();
    private static Map<String, String> banks = Maps.newHashMap();

    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> bankCollection;
    private MongoCollection<Document> subBankCollection;

    public SpiderService(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
        this.bankCollection = mongoDatabase.getCollection("t_bank");
        this.subBankCollection = mongoDatabase.getCollection("t_sub_bank");
    }


    static {
        provinces.put("1000", "北京市");
        provinces.put("1100", "天津市");
        provinces.put("1210", "河北省");
        provinces.put("1610", "山西省");
        provinces.put("1910", "内蒙古自治区");
        provinces.put("2210", "辽宁省");
        provinces.put("2410", "吉林省");
        provinces.put("2610", "黑龙江省");
        provinces.put("2900", "上海市");
        provinces.put("3010", "江苏省");
        provinces.put("3310", "浙江省");
        provinces.put("3610", "安徽省");
        provinces.put("3910", "福建省");
        provinces.put("4210", "江西省");
        provinces.put("4510", "山东省");
        provinces.put("4910", "河南省");
        provinces.put("5210", "湖北省");
        provinces.put("5510", "湖南省");
        provinces.put("5810", "广东省");
        provinces.put("6110", "广西壮族自治区");
        provinces.put("6410", "海南省");
        provinces.put("6510", "四川省");
        provinces.put("6530", "重庆市");
        provinces.put("7010", "贵州省");
        provinces.put("7310", "云南省");
        provinces.put("7700", "西藏自治区");
        provinces.put("7910", "陕西省");
        provinces.put("8210", "甘肃省");
        provinces.put("8510", "青海省");
        provinces.put("8710", "宁夏回族自治区");
        provinces.put("8810", "新疆维吾尔自治区");

        cityAlias.put("果洛州", "果洛藏族自治州");
        cityAlias.put("玉树州", "玉树藏族自治州");
        cityAlias.put("海西州", "海西蒙古族藏族自治州");
        cityAlias.put("马尔康县", "阿坝藏族羌族自治州");
        cityAlias.put("康定县", "甘孜藏族自治州");
        cityAlias.put("西昌市", "凉山彝族自治州");
        cityAlias.put("北京市", "北京城区");
        cityAlias.put("呼伦贝尔市（海拉尔区）", "呼伦贝尔市");
        cityAlias.put("乌兰察布盟", "乌兰察布市");
        cityAlias.put("巴彦淖尔盟（临河市）", "巴彦淖尔市");
        cityAlias.put("上海市", "上海城区");
        cityAlias.put("长冶市", "长治市");
        cityAlias.put("巢湖市", "合肥市");
        cityAlias.put("兴义市", "黔西南布依族苗族自治州");
        cityAlias.put("凯里市", "黔东南苗族侗族自治州");
        cityAlias.put("都匀市", "黔南布依族苗族自治州");
        cityAlias.put("个旧市", "红河哈尼族彝族自治州");
        cityAlias.put("文山县", "文山壮族苗族自治州");
        cityAlias.put("思茅市", "普洱市");
        cityAlias.put("景洪市", "西双版纳傣族自治州");
        cityAlias.put("大理市", "大理白族自治州");
        cityAlias.put("潞西市", "德宏傣族景颇族自治州");
        cityAlias.put("丽江县", "丽江市");
        cityAlias.put("泸水县", "怒江傈僳族自治州");
        cityAlias.put("香格里拉县", "迪庆藏族自治州");
        cityAlias.put("临沧县", "临沧市");
        cityAlias.put("襄樊市", "襄阳市");
        cityAlias.put("恩施州", "恩施土家族苗族自治州");
        cityAlias.put("株州市", "株洲市");
        cityAlias.put("吉首市", "湘西土家族苗族自治州");
        cityAlias.put("延吉市", "延边朝鲜族自治州");
        cityAlias.put("天津市", "天津城区");
        cityAlias.put("莱芜市", "济南市");

        cityAlias.put("临夏州", "临夏回族自治州");
        cityAlias.put("甘南州", "甘南藏族自治州");
        cityAlias.put("鹰谭市", "鹰潭市");
        cityAlias.put("重庆市", "重庆城区");
        cityAlias.put("万州区", "重庆城区");
        cityAlias.put("涪陵区", "重庆城区");
        cityAlias.put("黔江区", "重庆城区");
        cityAlias.put("北屯县", "北屯市");
        cityAlias.put("阿克苏市", "阿克苏地区");
        cityAlias.put("昌吉市", "昌吉回族自治州");
        cityAlias.put("博乐市", "博尔塔拉蒙古自治州");
        cityAlias.put("库尔勒市", "巴音郭楞蒙古自治州");
        cityAlias.put("阿图什市", "克孜勒苏柯尔克孜自治州");
        cityAlias.put("喀什市", "喀什地区");
        cityAlias.put("和田市", "和田地区");
        cityAlias.put("伊宁市", "伊犁哈萨克自治州");
        cityAlias.put("塔城市", "塔城地区");
        cityAlias.put("阿勒泰市", "阿勒泰地区");
        cityAlias.put("海北州", "海北藏族自治州");
        cityAlias.put("三道岭县", "哈密市");
        cityAlias.put("黄南州", "黄南藏族自治州");
        cityAlias.put("海南州", "海南藏族自治州");
        cityAlias.put("楚雄市", "楚雄彝族自治州");
        cityAlias.put("昌都县", "昌都市");
        cityAlias.put("乃东县", "山南市");
        cityAlias.put("那曲县", "那曲市");
        cityAlias.put("普兰县", "阿里地区");
        cityAlias.put("林芝县", "林芝市");

        banks.put("中国银行", "104");
        banks.put("中国工商银行", "102");
        banks.put("中国农业银行", "103");
        banks.put("中国建设银行", "105");
        banks.put("交通银行", "301");
        banks.put("招商银行", "308");
        banks.put("华夏银行", "304");
        banks.put("渤海银行", "318");
        banks.put("广发银行", "306");
        banks.put("平安银行", "307");
        banks.put("中信银行", "302");
        banks.put("中国光大银行", "303");
        banks.put("中国民生银行", "305");
        banks.put("兴业银行", "309");
        banks.put("上海浦东发展银行", "310");
        banks.put("中国邮政储蓄银行", "403");
        banks.put("其他银行", "-1");


    }

    public void run() throws IOException, JDOMException {
        for (String code : provinces.keySet()) {
            String provinceName = provinces.get(code);
            List<Map<String, String>> cities = findCities(code);
            for (Map<String, String> city : cities) {
                String cityName = city.get("cityName");
                cityName = cityAlias.getOrDefault(cityName, cityName);
                for (String bankName : banks.keySet()) {
                    List<Map<String, String>> remoteBanks = findBanks(banks.get(bankName), city.get("cityCode"));
                    for (Map<String, String> remoteBank : remoteBanks) {
                        String subBankName = remoteBank.get("bankName");
                        String unionBankNo = remoteBank.get("unionBankNo");
                        //电子银行跳过
                        String firstChar = unionBankNo.substring(0, 1);
                        if ("9".equals(firstChar)) {
                            continue;
                        }
                        if (subBankName.contains("备付金账户")) {
                            continue;
                        }

                        Boolean exist = existUnionBankNo(unionBankNo);
                        if (exist == false) {
                            SubBank subBank = new SubBank();
                            subBank.setProvince(provinceName);
                            subBank.setCity(cityName);
                            if (Objects.nonNull(subBankName) && Objects.nonNull(unionBankNo)) {
                                processBankName(subBank, subBankName);
                                subBank.setSubBankName(subBankName);
                                subBank.setUnionBankNo(unionBankNo);

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("province", subBank.getProvince());
                                map.put("city", subBank.getCity());
                                map.put("bank_id", subBank.getBankId());
                                map.put("bank_name", subBank.getBankName());
                                map.put("sub_bank_name", subBank.getSubBankName());
                                map.put("union_bank_no", subBank.getUnionBankNo());
                                ObjectId id = new ObjectId();
                                map.put("_id", id);
                                Document document;
                                document = new Document(map);
                                subBankCollection.insertOne(document);
                            }

                        }
                    }
                }
            }
        }
    }

    private boolean existUnionBankNo(String unionBankNo) {
        BasicDBObject where = new BasicDBObject();
        where.append("union_bank_no", unionBankNo);
        return subBankCollection.countDocuments(where) > 0 ? true : false;
    }


    private void processBankName(SubBank subBank, String subBankName) {
        String[] split = subBankName.split("银行");
        if (split.length > 0) {
            String bankName = split[0];
            if (bankName.equals("人民")) {
                bankName = "中国人民";
            }
            Boolean exist = existBank(bankName + "银行");
            if (exist == false) {
                if (subBankName.contains("银行") &&
                        StrUtil.isNotEmpty(bankName) &&
                        !subBankName.contains("地区)") &&
                        !subBankName.contains("地区）") &&
                        !subBankName.contains("（CIPS专用）")) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", bankName + "银行");
                    ObjectId id = new ObjectId();
                    map.put("_id", id);
                    Document document;
                    document = new Document(map);
                    bankCollection.insertOne(document);
                    subBank.setBankId(id.toString());
                    subBank.setBankName(bankName + "银行");
                }
            } else {
                BasicDBObject where = new BasicDBObject();
                where.append("name", bankName + "银行");
                MongoCursor<Document> cursor = bankCollection.find(where).limit(1).iterator();
                String id = null;
                while(cursor.hasNext()){
                    id = cursor.next().getObjectId("_id").toString();
                }
                subBank.setBankId(id);

                subBank.setBankName(bankName + "银行");
            }
        }

    }

    private boolean existBank(String bankName) {
        BasicDBObject where = new BasicDBObject();
        where.append("name", bankName);
        return bankCollection.countDocuments(where) > 0 ? true : false;
    }


    private List<Map<String, String>> findCities(String provinceCode) throws IOException, JDOMException {
        String url = provinceApi + "?provinceCode=" + provinceCode;
        String xml = HttpUtil.getHtml(url);
        xml = xml.substring(xml.indexOf("<kColl"), xml.length());  //截取返回信息，从kColl开始解析
        return XmlUtil.xml2List(xml, "iCityInfo");
    }

    private List<Map<String, String>> findBanks(String bankNo, String cityCode) {
        cityCode = cityCode.substring(0, cityCode.length() - 2);
        String url = bankApi + "?bankType=" + bankNo + "&cityCode=" + cityCode;

        String xml = HttpUtil.getHtml(url);
        xml = xml.substring(xml.indexOf("<kColl"), xml.length());
        return XmlUtil.xml2List(xml, "iBankInfo");
    }


}
