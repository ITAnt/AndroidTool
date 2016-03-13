# AndroidTool
安卓开发中用到的一些实用工具方法集合

本项目包含许多常用的工具方法，如系统相关的、应用相关的、命令行相关的、网络相关的、文件相关的、日期相关的、正则相关的、UI相关的等等。


![image](https://github.com/ITAnt/AndroidTool/blob/iTant/screenshots/结构图.png)


使用方法也非常简单：

1.将本项目导入编辑器，引用本项目。

2.所有的工具都由ToolFactory工厂产生，比如要使用DateTool中的获取当前日期方法，只需要两行代码：

DateTool dateTool = ToolFactory.getInstance().produceDateTool();

Toast.makeText(this, dateTool.getCurrentDate(), Toast.LENGTH_SHORT).show();

如果你不知道有哪些方法，直接使用ToolFactory加一个.，然后编辑器就会提示你有哪些方法了。
