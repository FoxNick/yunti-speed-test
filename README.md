# yunti-speed-test
批量测试云梯VPN各服务器的速度，以方便在不同的网络环境下选择合适的服务器。

* Gradle 构建项目
* HttpClient、jsoup 抓取、解析网页

命令`gradle pack`能自动打包 jar 和 init.properties 到 release 文件夹
```
task pack(type: Copy) {
    from 'build/libs'
    into 'release'
}

task copyConfig(type: Copy) {
    from 'init.properties'
    into 'release'
}

pack.dependsOn 'build', 'copyConfig'
```

## TODO
- ping命令测速
  - [x] Mac平台
  - [ ] Windows

## init.properties配置文件说明
```
# 云梯服务器地址，注意地址最后没有/
srvurl=https://www.ttincloud.com

# 登录云梯的账号密码，访问服务器列表需要先登录
username=test
password=password

# 测速使用的命令，暂时只支持ping
method=ping

# 仅在method=ping时有效，表示发送的ping包个数
ping_count=4
```

## 使用说明
0. 本地命令行有jdk/jre环境
1. 命令行下cd到`release`文件夹
2. 修改`init.properties` 中的配置
3. 命令行中运行 `java -jar yunti-speed-test.jar` 测速
4. 输出形如
```
aaron67-mbp:release aaron67$ java -jar yunti-speed-test.jar
读取配置文件
登录云梯网站
登录成功
抓取服务器列表成功
开始PING测速
启动28个线程测速
测速结束
-------------------------------------------------------------------------
    Server  Protocol       PKG     Miss%       MIN       MAX       AVG
-------------------------------------------------------------------------
      日本1号      PPTP         4       0.0    66.816    69.501     67.81
      日本1号      L2TP         4       0.0     68.64     69.57    69.085
      台湾1号      PPTP         4       0.0    88.484    88.728      88.6
      台湾1号      L2TP         4       0.0    88.608     89.02    88.716
      香港1号      PPTP         4       0.0   111.524   116.586   114.275
      香港1号      L2TP         4       0.0   119.956   143.966    133.52
      香港2号      L2TP         4       0.0   147.757   148.541   148.177
      美国4号      L2TP         4       0.0   156.406   158.206   157.407
      日本3号      PPTP         4       0.0   170.494   171.519   171.075
      日本3号      L2TP         4       0.0   171.379   172.954   172.218
      美国5号      PPTP         4       0.0   167.791   193.635   177.817
      美国1号      L2TP         4       0.0   181.268   184.595   183.301
      美国4号      PPTP         4       0.0   185.526   186.305   185.865
      美国2号      L2TP         4       0.0   198.575   199.828   199.062
      美国3号      L2TP         4       0.0   216.036   257.486   241.703
      美国3号      PPTP         4       0.0   222.255     256.6   242.754
      美国5号      L2TP         4       0.0   247.647   248.525    248.16
     新加坡2号      PPTP         4       0.0   235.371   276.514   258.417
     新加坡2号      L2TP         4       0.0   237.071   278.876   260.683
      美国1号      PPTP         4       0.0   245.687   282.063   268.452
      英国1号      L2TP         4       0.0   270.359   273.698   272.434
     新加坡1号      L2TP         4       0.0    283.46   324.142    308.34
     新加坡1号      PPTP         4       0.0   310.543   316.853   313.578
      美国2号      PPTP         4       0.0   333.284   368.872   353.555
      日本2号      PPTP         4      25.0   182.574   184.086   183.086
      英国1号      PPTP         4      25.0   267.552   269.259   268.388
      日本2号      L2TP         4      25.0    268.77   281.493   276.314
      香港2号      PPTP         4      50.0     84.71    86.261    85.486
-------------------------------------------------------------------------
aaron67-mbp:release aaron67$
```

## 贡献者列表
[itcrazyjin](https://github.com/itcrazyjin)
