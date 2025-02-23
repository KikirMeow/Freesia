# 香雪兰
[![Issues](https://img.shields.io/github/issues/KikirMeow/Freesia?style=flat-square)](https://github.com/KikirMeow/Freesia/issues)
![Commit Activity](https://img.shields.io/github/commit-activity/w/KikirMeow/Freesia?style=flat-square)
![GitHub Repo stars](https://img.shields.io/github/stars/KikirMeow/Freesia?style=flat-square)
![GitHub License](https://img.shields.io/github/license/KikirMeow/Freesia)

Cyanidin的延续版(正在施工中)

# 简介
这个项目是曾经的Cyanidin,但MrHua269放弃了Cyanidin这个项目,据他本人描述他并不想在ysm这个混乱的社区里继续做第三方开发了,这个项目只是在给他徒增压力,他不会继续维护了,偶然一天看见了这个项目,所以我便询问他要了过来

# 文档
这个项目由于一开始并没有多少服务器使用所以MrHua269也没写文档,可能我后续会补充上<del>(头大)</del>

# 构建
```shell
chmod +777 ./gradlew
./gradlew build
```

```shell
gradlew.bat build
```

构建产物分别在每个模块的build/libs下

# 原理
简单来说的话,这东西其实更像是一个MultiPaper和Geyser的混合体,项目的工作原理也是参考的multipaper的跨服数据交换的机制,对于部分数据包由于需要判断玩家是否能看见玩家是否看得见其他玩家并且worker和子服的实体id不一样就修改了一下

# 性能
这东西? <del>我也没测试过说实话</del>,不过据MrHua269的之前留下的文档来看,带130人貌似没问题但是他本人和我说很大情况下ysm自己的缓存同步会出现内存泄漏之类的奇奇怪怪的问题然后爆炸掉导致模型同步无法正常工作

# 路线图
这个我还没有思路, Cyanidin的src我并没有完全理解所以我还需要花时间来熟悉这个项目
目前的计划打算是尝试做多worker的支持以及完成未完成的npc插件的拓展(可能会单开几个新项目罢)
