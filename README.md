# JSavON
A simple java JSON deserializer that can convert a JSON into a java object in an easy way
This library provide a strong object convertion 

# Deserializing JSONObject
We have this JSONObject : 
```json
{
  "orderListId": 29,
  "contingencyType": "OCO",
  "listClientOrderId": "amEEAXryFzFwYF1FeRpUoZ",
  "transactionTime": 1565245913483,
  "symbol": "LTCBTC",
  "orders": [
    {
      "symbol": "LTCBTC",
      "orderId": 4,
      "clientOrderId": "oD7aesZqjEGlZrbtRpy5zB"
    },
    {
      "symbol": "LTCBTC",
      "orderId": 5,
      "clientOrderId": "Jr1h6xirOxgeJOUuYQS7V3"
    }
  ],
  "data" : {
    "total":20
  }
},
```
The corresponding java object will be : 
```java
public class MyObject extends JSavONObject {

  //The JSavON code will search in the json the corresponding key (the field name)
  //By default, the key will be the field name
  private int orderListId;
  private long transactionTime;
  //But you can change the key using
  @JOValue(keys = "contingencyType") private MyEnum ct;
  //You can also use multiple keys
  @JOValue(keys = {"listClientOrderId", "listCOI"}) private String listClientOrderId;
  
  //You can set manual value using the annotation ManualValue
  @ManualValue private long time;
  
  //You can also use a path for retrieving value
  @JOValue(paths = {"data"}) private int total;
  
  //Indicate that this field is not mandatory (default=true) 
  @JOValue(mandatory = false) private String symbol;
  
  
  @MapKey(fieldName = "symbol") private Map<String, Order> orders;
  
  //Constructor
  public MyObject(JSONObject json) {
    super(json);
    this.time = System.currentTimeMillis();
  }
  
  //Getters and Setters
  ...
}
```

# Information

This library is mainly used in JBinanceAPI but you can use it for any other project
There are a lot more you can do with this library, like customizing retrieving algorithm, parsing JSONArray...

# Donation
I'm a student, so any help will be appreciated
### For gentleman
BTC : 1JJZrkZEynrCqoPrJWKbtGLSyJziDmKKka
### For optimistics
ETH : 0xe349abf167a8d265785b256666737ecd5720d4bd
### BNB Love
BNB : bnb136ns6lfw4zs5hg4n85vdthaad7hq5m4gtkgf23  
TAG : 107019672
### Some XRP ?
XRP : rEb8TK3gBgk5auZkwc6sHnwrGVJH8DuaLh  
TAG : 109836101
### Maybe you prefer ADA ?
ADA : DdzFFzCqrht7QmsyECRJsZiM3neGxrGFjhrkDHjCnjT1w45oeVeNKf1KwBQWcVcPCcAuz7kfzbMhTG8BEpgX7FRy25XdTsk4RfM6YXqC
