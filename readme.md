# AliceBot聊天机器人

AliceBot是一款Java版的AIML1.0实现

本项目支持中文/英文聊天。需要导入对应的预料。详细的需要学习AIML语言


# 使用


将数据库sql导入到MySQL中，并在Alice项目根目录中修改robot.properties里面的数据库配置信息。

关于程序在eclipse控制台的运行：
1.其主函数入口在com.context包中的，Robot.java类中
2.用户的输入不是从控制台输入（这是因为考虑到编码问题），而是从Alice根目录下的myinput.txt文件中输入。

你先想好你将要说什么，
比如你在myinput.txt文件中输入：
你好
今天天气不错
你是谁
...

然后你运行Robot.java这个类
机器人就会对myinput.txt中的每一句做一个回答，并输出到控制台。

这样做可能不太方便，但因为之前从控制台输入，会因为编码问题导致机器人的学习功能不能正常工作，所以暂时先这么做，以后有需要在做修改。
