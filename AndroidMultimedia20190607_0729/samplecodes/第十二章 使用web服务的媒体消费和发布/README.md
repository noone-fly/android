1，http请求

算base64


import javax.xml.bind.DatatypeConverter;
String encoding = DatatypeConverter.printBase64Binary("118f0fc996234f50bd2c8626e17dfd42:a840047a26fb408dbc11f1853f4c393b".getBytes("UTF-8"));
		
MTE4ZjBmYzk5NjIzNGY1MGJkMmM4NjI2ZTE3ZGZkNDI6YTg0MDA0N2EyNmZiNDA4ZGJjMTFmMTg1M2Y0YzM5M2I=

import android.util.Base64;

String encoding = Base64.encodeToString("118f0fc996234f50bd2c8626e17dfd42:a840047a26fb408dbc11f1853f4c393b".getBytes(Charset.forName("utf-8")), Base64.NO_WRAP);


V/base64 string: 
MTE4ZjBmYzk5NjIzNGY1MGJkMmM4NjI2ZTE3ZGZkNDI6YTg0MDA0N2EyNmZiNDA4ZGJjMTFmMTg1M2Y0YzM5M2I=



2，json

这是一个json对象，json对象名称：result
{"result":{"aname":"value", "anumber":"123", "aboolean":"false"}}
{
"result":
{
"aname":"value", 
"anumber":"123", 
"aboolean":"false"
}
}


json数据数组，包含多个json对象
数组名称：projects
多个json对象置于方括号内，以逗号相隔


V/http request:
 {"projects":[
{
"id":"Hyx8eTdGM",
"name":"IOS",
"vendor_key":"fd33b9******79efff42701",
"sign_key":"",
"recording_server":null,
"status":1,
"created":1513832520447
}
]}


{
    "projects": [
        {
            "id": "Sk2lre_BV",
            "name": "123453",
            "vendor_key": "db473e42a26340******ddcf6f45315",
            "sign_key": "625bea06fac******f60833ddca229",
            "recording_server": null,
            "status": 0,
            "created": 1550480628430
        },
        {
            "id": "B1ylreuHN",
            "name": "12345",
            "vendor_key": "eac691bcae134******c235b9f7a7",
            "sign_key": "774d9b9fe0******6c57fd2b3cc04",
            "recording_server": null,
            "status": 0,
            "created": 1550480614978
        },
        
    ]
}



使用android解析json数据
android的JSONObject 类，可以通过传入json格式的数据来构造
1，把一个实际的json字符串构造成 JSONObject 对象
JSONObject jsonObject = new JSONObject(jsondata);

2，然后通过方法 getJSONObject("result"); 获取json的引用
JSONObject resultJson = jsonObject.getJSONObject("result"); 

3，使用 getString 获取具体数据
String name = resultJson.getString("aname");

4，同理  getInt(), getDouble(), getLong(),  getBoolean()


5，使用 getJSONArray 获取数组
JSONArray jsonArray =  jsonObject.getJSONArray("projects");

6，然后 数组中的单个json对象 getJSONObject
for(int i = 0; i < jsonArray.length(); i++){
   JSONObject arrayElementObject = jsonArray.getJSONObject(i);
   String arrayElement = arrayElementObject.getString("");
}



http://www.flickr.com/




RESTful
https://www.runoob.com/w3cnote/restful-architecture.html
representational state transfer 表述性状态转移